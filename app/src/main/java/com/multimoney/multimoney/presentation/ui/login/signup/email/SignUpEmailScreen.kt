package com.multimoney.multimoney.presentation.ui.login.signup.email

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.SignUpStep
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onMessage
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.extension.findActivity
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.Companion.ISO3_COSTA_RICA
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnShowCloseIcon
import com.multimoney.multimoney.presentation.ui.login.signup.email.SignUpEmailViewModel.UIEvent.OnShowAnotherDeviceAlreadyRegisteredDialog
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.util.catalog.AdjustEventType
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.util.firebase.FireBaseEvents

@Composable
@Preview
fun SignUpEmailScreen(
    viewModel: SignUpEmailViewModel = hiltViewModel(),
    sharedViewModel: SignUpViewModel = hiltViewModel()
) {
    // Properties
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val activity = LocalContext.current.findActivity() as FragmentActivity

    LaunchedEffect(true) {
        sharedViewModel.onUIEvent(OnShowCloseIcon(false))
        sharedViewModel.onUIEvent(
            SignUpViewModel.UIEvent.OnSetNavigation(nextAction = {
                viewModel.onUIEvent(
                    SignUpEmailViewModel.UIEvent.OnNextActionClick(
                        activity = activity,
                        nextStepAction = {
                            sharedViewModel.onUIEvent(
                                SignUpViewModel.UIEvent.OnNextStep
                            )
                        }
                    )
                )
                sharedViewModel.logEvents(FireBaseEvents.SignUpOne, AdjustEventType.SIGNUP_1_2001)
            }, nextStep = SignUpStep.Two.id, previousStep = SignUpStep.One.id)
        )
        viewModel.baseEvent.collect { event ->
            when (event) {
                is SignUpEmailViewModel.BaseEvent.OnFormValidateCompleted -> sharedViewModel.onUIEvent(
                    SignUpViewModel.UIEvent.OnContinueEnable(
                        event.isFormValid
                    )
                )
            }
        }
    }

    LaunchedEffect(true) {
        viewModel.onUIEvent(SignUpEmailViewModel.UIEvent.OnValidateForm)
        viewModel.onValidateUserExistsEvent.collect { event ->
            event.onSuccess { userData ->
                sharedViewModel.onUIEvent(SignUpViewModel.UIEvent.OnSetIdBrand(userData?.idBrand ?: 0))
                sharedViewModel.onUIEvent(
                    SignUpViewModel.UIEvent.OnLoadingValueChange(
                        false
                    )
                )
                if (userData?.status == SignUpEmailViewModel.ANOTHER_DEVICE_ALREADY_REGISTERED) {
                    viewModel.onUIEvent(
                        OnShowAnotherDeviceAlreadyRegisteredDialog {
                            viewModel.onSuccessValidation(context, sharedViewModel, userData)
                        }
                    )
                    sharedViewModel.logEvents(null, AdjustEventType.SECURITY_SIGN_UP_CHANGE_DEVICE_9001)
                } else {
                    viewModel.onSuccessValidation(context, sharedViewModel, userData)
                }
            }.onMessage {
                viewModel.onUIEvent(
                    SignUpEmailViewModel.UIEvent.OnHandleUserStatus(
                        context = context,
                        userData = it,
                        previousStepAction = { sharedViewModel.onUIEvent(SignUpViewModel.UIEvent.OnPreviousStep) },
                        onLoadingValueChange = {
                            sharedViewModel.onUIEvent(
                                SignUpViewModel.UIEvent.OnLoadingValueChange(
                                    false
                                )
                            )
                        },
                        onOpenDialog = { dialog ->
                            sharedViewModel.onUIEvent(
                                SignUpViewModel.UIEvent.OnOpenDialogValueChange(
                                    dialog
                                )
                            )
                        }
                    )
                )
            }.onFailure {
                sharedViewModel.onUIEvent(
                    SignUpViewModel.UIEvent.OnFailureWithDialog(
                        isLoading = false,
                        openDialog = DialogParameters(
                            titleResource = string.error_empty,
                            description = it.getError() ?: "",
                            isActive = mutableStateOf(true)
                        )
                    )
                )
            }.onLoading {
                sharedViewModel.onUIEvent(SignUpViewModel.UIEvent.OnLoadingValueChange(true))
            }
        }
    }

    viewModel.onUIEvent(
        SignUpEmailViewModel.UIEvent.OnStart(
            userCompletedDialogDescription = stringResource(id = string.sign_up_email_user_completed_dialog_description),
            sharedViewModel.whatsAppLink ?: "",
            blockedMessage = stringResource(id = string.sign_up_email_blocked_dialog_description)
        )
    )

    Column(modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp)) {
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = Typography.h4.toSpanStyle()
                        .copy(
                            color = MultimoneyTheme.colors.titleText,
                            fontWeight = FontWeight.SemiBold
                        )
                ) {
                    append(stringResource(id = string.sign_up_email_title))
                }
            },
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth(),
            lineHeight = 36.sp
        )

        // Fields
        CustomOutlinedTextField(
            value = viewModel.uiState.userEmail,
            onValueChange = { value ->
                viewModel.onUIEvent(
                    SignUpEmailViewModel.UIEvent.OnUserEmailValueChange(
                        value
                    )
                )
            },
            onDebounceValidation = { viewModel.onUIEvent(SignUpEmailViewModel.UIEvent.OnValidateUserEmail) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            }),
            labelText = stringResource(id = if (sharedViewModel.uiState.isO3Country == ISO3_COSTA_RICA) string.sign_up_email_header else string.sign_up_email_header_sv),
            placeHolder = stringResource(id = string.sign_up_email_placeholder),
            modifier = Modifier.padding(top = 24.dp),
            isRequired = true,
            isRequiredMessage = stringResource(id = string.sign_up_email_required),
            isError = viewModel.uiState.userEmailError.first,
            errorMessage = stringResource(id = viewModel.uiState.userEmailError.second)
        )
    }
    if (viewModel.uiState.openDialog.isActive.value) {
        CustomDialog(
            title = stringResource(id = viewModel.uiState.openDialog.titleResource),
            message = stringResource(id = viewModel.uiState.openDialog.descriptionResource),
            positiveButtonText = stringResource(id = viewModel.uiState.openDialog.positiveResource),
            negativeButtonText = stringResource(id = viewModel.uiState.openDialog.negativeResource),
            openDialogCustom = viewModel.uiState.openDialog.isActive,
            onPositiveAction = viewModel.uiState.openDialog.positiveAction
        )
    }
}
