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
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.SignUpStep
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.DefaultWhite
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnContinueEnable
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnFailureWithDialog
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnLoadingValueChange
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnMoveToStep
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnNextActionValueChange
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnNextStep
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnOpenDialogValueChange
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnPreviousStep
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnUseDataValueChange
import com.multimoney.multimoney.presentation.ui.login.signup.email.SignUpEmailViewModel.BaseEvent.OnFormValidateCompleted
import com.multimoney.multimoney.presentation.ui.login.signup.email.SignUpEmailViewModel.UIEvent.OnNextActionClick
import com.multimoney.multimoney.presentation.ui.login.signup.email.SignUpEmailViewModel.UIEvent.OnStart
import com.multimoney.multimoney.presentation.ui.login.signup.email.SignUpEmailViewModel.UIEvent.OnUserDataValidationSuccess
import com.multimoney.multimoney.presentation.ui.login.signup.email.SignUpEmailViewModel.UIEvent.OnUserEmailValueChange
import com.multimoney.multimoney.presentation.ui.login.signup.email.SignUpEmailViewModel.UIEvent.OnValidateForm
import com.multimoney.multimoney.presentation.ui.login.signup.email.SignUpEmailViewModel.UIEvent.OnValidateUserEmail
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.util.DialogParameters

@Composable
@Preview
fun SignUpEmailScreen(
    viewModel: SignUpEmailViewModel = hiltViewModel(),
    sharedViewModel: SignUpViewModel = hiltViewModel()
) {

    // Properties
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    LaunchedEffect(true) {
        sharedViewModel.onUIEvent(OnNextActionValueChange {
            viewModel.onUIEvent(OnNextActionClick {
                sharedViewModel.onUIEvent(
                    OnNextStep
                )
            })
        })

        viewModel.baseEvent.collect { event ->
            when (event) {
                is OnFormValidateCompleted -> sharedViewModel.onUIEvent(OnContinueEnable(event.isFormValid))
            }
        }
    }

    LaunchedEffect(true) {
        viewModel.onUIEvent(OnValidateForm)

        viewModel.onUserDataValidationEvent.collect { event ->
            event.onSuccess { userData ->
                viewModel.onUIEvent(
                    OnUserDataValidationSuccess(
                        context = context,
                        currentStep = sharedViewModel.uiState.currentStep,
                        userData = userData,
                        onUseDataValueChange = { sharedViewModel.onUIEvent(OnUseDataValueChange(userData)) },
                        nextStepAction = { sharedViewModel.onUIEvent(OnNextStep) },
                        moveToStepAction = {
                            sharedViewModel.onUIEvent(
                                OnMoveToStep(
                                    SignUpStep.Search.getIdByName(
                                        userData?.currentStep
                                    )
                                )
                            )
                        },
                        previousStepAction = { sharedViewModel.onUIEvent(OnPreviousStep) },
                        onLoadingValueChange = { sharedViewModel.onUIEvent(OnLoadingValueChange(false)) },
                        onOpenDialog = { dialog -> sharedViewModel.onUIEvent(OnOpenDialogValueChange(dialog)) }
                    )
                )
            }.onFailure {
                sharedViewModel.onUIEvent(
                    OnFailureWithDialog(
                        isLoading = false,
                        openDialog = DialogParameters(
                            description = it.getError() ?: "",
                            isActive = mutableStateOf(true)
                        )
                    )
                )
            }.onLoading {
                sharedViewModel.onUIEvent(OnLoadingValueChange(true))
            }
        }
    }

    viewModel.onUIEvent(
        OnStart(
            userCompletedDialogDescription = stringResource(id = R.string.sign_up_email_user_completed_dialog_description),
            linkWhatsapp = stringResource(
                id = R.string.whatsapp_deep_link,
                SignUpViewModel.PHONE_HARDCODED
            ),
            blockedMessage = stringResource(id = R.string.sign_up_email_blocked_dialog_description)
        )
    )

    Column(modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp)) {
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = Typography.h4.toSpanStyle()
                        .copy(fontWeight = FontWeight.SemiBold, color = DefaultWhite)
                ) {
                    append(stringResource(id = R.string.sign_up_email_title))
                }
            },
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )

        // Fields
        CustomOutlinedTextField(
            value = viewModel.uiState.userEmail,
            onValueChange = { value -> viewModel.onUIEvent(OnUserEmailValueChange(value)) },
            onDebounceValidation = { viewModel.onUIEvent(OnValidateUserEmail) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            }),
            labelText = stringResource(id = R.string.sign_up_email_header),
            placeHolder = stringResource(id = R.string.sign_up_email_placeholder),
            modifier = Modifier.padding(top = 24.dp),
            isRequired = true,
            isRequiredMessage = stringResource(id = R.string.sign_up_email_required),
            isError = viewModel.uiState.userEmailError.first,
            errorMessage = stringResource(id = viewModel.uiState.userEmailError.second)
        )
    }
}