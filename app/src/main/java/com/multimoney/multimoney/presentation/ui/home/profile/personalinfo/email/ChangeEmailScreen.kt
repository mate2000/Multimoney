package com.multimoney.multimoney.presentation.ui.home.profile.personalinfo.email

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent

@Preview
@Composable
fun ChangeEmailScreen(
    onPopBackStack: ((NavEvent.PopBackStack)) -> Unit = {},
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    viewModel: ChangeEmailViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {
        viewModel.executeNavigation(onPopBackStack = onPopBackStack, onNavigate = onNavigate)
    }
    BackHandler {
        viewModel.onUIEvent(ChangeEmailViewModel.UIEvent.OnNavigateBack)
    }
    ChangePhoneScreenContent(viewModel)
}

@Composable
private fun ChangePhoneScreenContent(viewModel: ChangeEmailViewModel) {
    val focusManager = LocalFocusManager.current
    ConstraintLayout(
        modifier = Modifier
            .background(MultimoneyTheme.colors.background)
            .fillMaxSize()
    ) {
        val (topNavBar, headerColumn, continueButton) = createRefs()

        TopNavBar(
            modifier = Modifier.constrainAs(topNavBar) {
                top.linkTo(parent.top)
            },
            onLeftButtonClick = {
                viewModel.onUIEvent(ChangeEmailViewModel.UIEvent.OnNavigateBack)
            },
            isRightButtonVisible = false
        )

        Column(
            modifier = Modifier
                .constrainAs(headerColumn) {
                    top.linkTo(topNavBar.bottom)
                    bottom.linkTo(continueButton.top)
                    height = Dimension.fillToConstraints
                }
                .padding(horizontal = 16.dp)
        ) {
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = stringResource(id = R.string.profile_change_email_title),
                style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
                color = MultimoneyTheme.colors.labelText,
                textAlign = TextAlign.Left
            )
            // Fields
            CustomOutlinedTextField(
                value = viewModel.uiState.newEmail,
                onValueChange = { value ->
                    viewModel.onUIEvent(
                        ChangeEmailViewModel.UIEvent.OnUserEmailValueChange(
                            value
                        )
                    )
                },
                onDebounceValidation = { viewModel.onUIEvent(ChangeEmailViewModel.UIEvent.OnValidateUserEmail) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }),
                labelText = stringResource(id = R.string.profile_change_email_enter_new),
                placeHolder = stringResource(id = R.string.sign_up_email_placeholder),
                modifier = Modifier.padding(top = 24.dp),
                isRequired = true,
                isError = viewModel.uiState.userEmailError.first,
                isRequiredMessage = stringResource(id = R.string.sign_up_email_required)
            )
            // Fields
            CustomOutlinedTextField(
                value = viewModel.uiState.newEmailConfirmation,
                onValueChange = { value ->
                    viewModel.onUIEvent(
                        ChangeEmailViewModel.UIEvent.OnUserEmailConfirmationValueChange(
                            value
                        )
                    )
                },
                onDebounceValidation = { viewModel.onUIEvent(ChangeEmailViewModel.UIEvent.OnValidateUserEmailConfirmation) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                }),
                labelText = stringResource(id = R.string.profile_change_email_enter_new_confirmation),
                placeHolder = stringResource(id = R.string.sign_up_email_placeholder),
                modifier = Modifier.padding(top = 24.dp),
                isRequired = true,
                isRequiredMessage = stringResource(id = R.string.sign_up_email_required),
                isError = viewModel.uiState.userEmailError.first,
                errorMessage = stringResource(id = viewModel.uiState.userEmailError.second)
            )
            Text(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                text = stringResource(id = R.string.profile_we_will_send_you_a_code_to_your_email),
                style = Typography.body2.copy(color = MultimoneyTheme.colors.descriptionText)
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
            text = stringResource(id = R.string.profile_change_email_button),
            enable = viewModel.uiState.isButtonEnabled,
            onClick = {
                viewModel.onUIEvent(ChangeEmailViewModel.UIEvent.OnContinueButtonClicked)
            }
        )
    }
}
