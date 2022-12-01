package com.multimoney.multimoney.presentation.ui.home.profile.personalinfo.phone

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onMessage
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel
import com.multimoney.multimoney.presentation.ui.login.signup.otp.SignUpOtpViewModel
import com.multimoney.multimoney.presentation.ui.login.signup.otp.SignUpOtpViewModel.Companion.TOTAL_DIGITS
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.OtpTextField
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters


@Preview
@Composable
fun ValidateOTPScreen(
    onPopBackStack: ((NavEvent.PopBackStack)) -> Unit = {},
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    viewModel: ValidateOTPViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {
        viewModel.executeNavigation(onPopBackStack = onPopBackStack, onNavigate = onNavigate)
    }

    BackHandler {
        viewModel.onUIEvent(ValidateOTPViewModel.UIEvent.OnNavigateBack)
    }

    ValidateOTPContent(viewModel = viewModel)
    LoadingIndicator(viewModel.uiState.isLoading)
    if (viewModel.uiState.openDialog.isActive.value) {
        CustomDialog(
            title = stringResource(id = viewModel.uiState.openDialog.titleResource),
            message = viewModel.uiState.openDialog.description,
            positiveButtonText = stringResource(id = viewModel.uiState.openDialog.positiveResource),
            negativeButtonText = stringResource(id = viewModel.uiState.openDialog.negativeResource),
            openDialogCustom = viewModel.uiState.openDialog.isActive,
            onPositiveAction = viewModel.uiState.openDialog.positiveAction,
            onNegativeAction = viewModel.uiState.openDialog.negativeAction
        )
    }

    viewModel.onUIEvent(
        ValidateOTPViewModel.UIEvent.OnStart(
            stringResource(
                id = R.string.whatsapp_deep_link,
                SignUpViewModel.PHONE_HARDCODED
            ),
            stringResource(id = R.string.sign_up_otp_code_user_blocked_for_exceed_the_max_of_attend)
        )
    )


    LaunchedEffect(key1 = true){
        viewModel.onUIEvent(
            ValidateOTPViewModel.UIEvent.OnCallMutationSendPinProcess(
                viewModel.uiState.identification ?: "",
                viewModel.uiState.firstName ?: "",
                viewModel.uiState.email ?: "",
                viewModel.uiState.phoneNumber ?: "",
                SignUpOtpViewModel.SEND_METHOD_PHONE,
                viewModel.uiState.pkUser ?: "",
                viewModel.uiState.idBrand?: 0,
                viewModel.uiState.email ?: ""
            )
        )
    }

    LaunchedEffect(true) {
        viewModel.onCallMutationSendPinProcessEvent.collect { event ->
            event.onSuccess {
                viewModel.apply {
                    onUIEvent(ValidateOTPViewModel.UIEvent.OnLoadingValueChange(false))
                    onUIEvent(ValidateOTPViewModel.UIEvent.OnCallMutationSendPinProcessSuccess(it))
                }
            }.onMessage {
                    viewModel.onUIEvent(ValidateOTPViewModel.UIEvent.OnFailureWithDialog(false, DialogParameters(
                        titleResource = R.string.sign_up_email_blocked_dialog_title,
                        description = viewModel.userBlockedForMaxAttend,
                        isActive = mutableStateOf(true),
                        positiveResource = R.string.contact,
                        negativeResource = R.string.cancel,
                        negativeAction = {
                            viewModel.onUIEvent(ValidateOTPViewModel.UIEvent.OnNavigateToLogin)
                        }
                    )))
            }.onFailure {
                Log.e("TAG","failure")
                viewModel.onUIEvent(ValidateOTPViewModel.UIEvent.OnLoadingValueChange(false))
            }.onLoading {
                Log.e("TAG","loading")
                viewModel.onUIEvent(ValidateOTPViewModel.UIEvent.OnLoadingValueChange(true))
            }
        }
    }


}

@Composable
fun ValidateOTPContent(viewModel: ValidateOTPViewModel){
    ConstraintLayout(
        modifier = Modifier
            .background(MultimoneyTheme.colors.background)
            .fillMaxSize()
    ) {
        val (topNavBar, otpField, timerText,titleText,headerText,continueButton) = createRefs()

        TopNavBar(
            modifier = Modifier.constrainAs(topNavBar) {
                top.linkTo(parent.top)
            },
            onLeftButtonClick = {
                viewModel.onUIEvent(ValidateOTPViewModel.UIEvent.OnNavigateBack)
            },
            isRightButtonVisible = false
        )
        Text(
            modifier = Modifier
                .padding(top = 8.dp, start = 16.dp, end = 16.dp)
                .constrainAs(titleText) {
                    top.linkTo(topNavBar.bottom)
                },
            text = stringResource(id = R.string.profile_identity_verification),
            style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.labelText,
            textAlign = TextAlign.Left
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .constrainAs(headerText) {
                    top.linkTo(titleText.bottom)
                },
            text = stringResource(id = R.string.profile_enter_the_code_sent_to_template,viewModel.uiState.phoneNumber ?: "" )
        )
        OtpTextField(
            value = viewModel.uiState.otp,
            onValueChange = {
                viewModel.onUIEvent(ValidateOTPViewModel.UIEvent.OnOtpValueChange(it))
            },
            isValueFromSms = viewModel.uiState.isOtpFromSms,
            digits = TOTAL_DIGITS,
            placeHolder = stringResource(id = R.string.sign_up_otp_code_placeholder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp)
                .constrainAs(otpField) {
                    top.linkTo(headerText.bottom)
                },
            isRequired = true,
            isRequiredMessage = stringResource(id = R.string.sign_up_otp_code_required),
            isError = viewModel.uiState.otpError.first,
            errorMessage = stringResource(id = viewModel.uiState.otpError.second)
        )
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .constrainAs(timerText) {
                top.linkTo(otpField.bottom)
            }){
            Text(
                text = stringResource(id = R.string.profile_code_expires_in_template , viewModel.uiState.remainingTimeText),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 32.dp)
                    .fillMaxWidth(),
                style = Typography.body2.copy(
                    color = MultimoneyTheme.colors.timerColor,
                    fontWeight = FontWeight.SemiBold
                )
            )

        }

        CustomButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(horizontal = 16.dp)
                .constrainAs(continueButton) {
                    bottom.linkTo(parent.bottom, margin = 40.dp)
                },
            buttonType = CustomButtonType.PrimaryPrimary,
            text = stringResource(id = R.string.profile_send_code),
            enable = viewModel.isFormValid(),
            onClick = {
               // viewModel.onUIEvent(VerifyIdentityViewModel.UIEvent.OnContinueButtonClicked)
            }
        )

    }

}
