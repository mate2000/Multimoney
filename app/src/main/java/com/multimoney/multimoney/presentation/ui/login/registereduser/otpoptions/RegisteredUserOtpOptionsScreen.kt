package com.multimoney.multimoney.presentation.ui.login.registereduser.otpoptions

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.login.registereduser.otpoptions.RegisteredUserOtpOptionsViewModel.UIEvent.OnBackClick
import com.multimoney.multimoney.presentation.ui.login.registereduser.otpoptions.RegisteredUserOtpOptionsViewModel.UIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.login.registereduser.otpoptions.RegisteredUserOtpOptionsViewModel.UIEvent.OnOtpOptionSelected
import com.multimoney.multimoney.presentation.ui.login.registereduser.otpoptions.RegisteredUserOtpOptionsViewModel.UIEvent.OnStart
import com.multimoney.multimoney.presentation.ui.login.registereduser.otpoptions.RegisteredUserOtpOptionsViewModel.UIState
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.RadioButtonQuestion
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.catalog.SendOtpMethod

@Composable
fun RegisteredUserOtpOptionsScreen(
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: RegisteredUserOtpOptionsViewModel = hiltViewModel()
) {
    // Navigation
    LaunchedEffect(true) {
        viewModel.apply {
            executeNavigation(onNavigate = onNavigate, onPopBackStack = onPopBackStack)
            onUIEvent(OnStart)
        }
    }
    RegisteredUserOtpOptionsContent(
        uiState = viewModel.uiState,
        onOtpOptionSelected = { value -> viewModel.onUIEvent(OnOtpOptionSelected(value)) },
        onBackClick = { viewModel.onUIEvent(OnBackClick) },
        onContinueClick = { viewModel.onUIEvent(OnContinueClick) }
    )
}

@Composable
@Preview
fun RegisteredUserOtpOptionsContent(
    uiState: UIState = UIState(),
    onOtpOptionSelected: (String) -> Unit = {},
    onBackClick: () -> Unit = {},
    onContinueClick: () -> Unit = {}
) {
    Column(modifier = Modifier.background(MultimoneyTheme.colors.background).fillMaxSize()) {
        TopNavBar(
            isRightButtonVisible = false,
            onLeftButtonClick = { onBackClick() }
        )
        Column(
            modifier = Modifier.padding(horizontal = 16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = stringResource(uiState.titleResource),
                    modifier = Modifier.padding(top = 24.dp),
                    style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
                    color = MultimoneyTheme.colors.text
                )
                Text(
                    text = stringResource(uiState.messageResource),
                    modifier = Modifier.padding(top = 24.dp),
                    style = Typography.body1,
                    color = MultimoneyTheme.colors.labelText
                )
                RadioButtonQuestion(
                    firstButtonTextResource = string.registered_user_otp_options_email,
                    secondButtonTextResource = string.registered_user_otp_options_sms,
                    shouldHaveDisclaimer = false,
                    firstButtonIsSelected = uiState.otpOption == SendOtpMethod.Email.value,
                    secondButtonIsSelected = uiState.otpOption == SendOtpMethod.Sms.value,
                    onFirstButtonOnClick = {
                        onOtpOptionSelected(SendOtpMethod.Email.value)
                    },
                    onSecondButtonOnClick = {
                        onOtpOptionSelected(SendOtpMethod.Sms.value)
                    }
                )
            }
            CustomButton(
                onClick = { onContinueClick() },
                enable = uiState.isFormValid,
                text = stringResource(id = string.registered_user_otp_options_button),
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .fillMaxWidth()
                    .height(48.dp)
            )
        }
    }

    BackHandler {
        onBackClick()
    }
}
