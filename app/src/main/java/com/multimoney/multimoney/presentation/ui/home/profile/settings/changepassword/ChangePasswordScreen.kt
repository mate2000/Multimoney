package com.multimoney.multimoney.presentation.ui.home.profile.settings.changepassword

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.flowlayout.FlowRow
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.login.signup.password.PasswordRequirementLabels
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun ChangePasswordScreen(
    onPopBackStack: ((NavEvent.PopBackStack)) -> Unit = {},
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    viewModel: ChangePasswordViewModel = hiltViewModel()
) {

    ChangePasswordContent(
        viewModel = viewModel
    )
    LoadingIndicator(viewModel.uiState.isLoading)

}

@Composable
fun ChangePasswordContent(viewModel: ChangePasswordViewModel) {
    val focusManager = LocalFocusManager.current
    Column(
        modifier = Modifier
            .background(MultimoneyTheme.colors.background)
            .fillMaxSize()
    ) {
        TopNavBar(
            onLeftButtonClick = {
                viewModel.onUIEvent(ChangePasswordViewModel.UIEvent.OnNavigateBack)
            },
            isRightButtonVisible = false
        )
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (contentColumn, changePasswordButton) = createRefs()
            Column(modifier = Modifier
                .padding(horizontal = 16.dp)
                .constrainAs(contentColumn) {
                    top.linkTo(parent.top)
                }) {
                Text(
                    modifier = Modifier.padding(top = 8.dp),
                    text = stringResource(id = R.string.profile_settings_change_password),
                    style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
                    color = MultimoneyTheme.colors.labelText,
                    textAlign = TextAlign.Left
                )
                CustomOutlinedTextField(
                    value = viewModel.uiState.currentPassword,
                    onValueChange = {
                        viewModel.onUIEvent(
                            ChangePasswordViewModel.UIEvent.OnCurrentPasswordValueChange(
                                it
                            )
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(onNext = {
                        focusManager.clearFocus()
                    }),
                    labelText = stringResource(id = R.string.profile_settings_current_password),
                    isPassword = true,
                    modifier = Modifier
                        .padding(top = 24.dp),
                    isRequired = true,
                    isRequiredMessage = stringResource(id = R.string.sign_up_password_required),
                    isError = viewModel.uiState.currentPasswordError.first,
                    errorMessage = if (viewModel.uiState.currentPasswordError.first) {
                        stringResource(id = viewModel.uiState.currentPasswordError.second)
                    } else {
                        null
                    }
                )

                ClickableText(
                    text = AnnotatedString(stringResource(id = R.string.profile_settings_forget_password)),
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .align(Alignment.End),
                    style = Typography.body2.copy(
                        textDecoration = TextDecoration.Underline,
                        color = MultimoneyTheme.colors.textLink
                    ),
                    onClick = { }
                )

                CustomOutlinedTextField(
                    value = viewModel.uiState.newPassword,
                    onValueChange = {
                        viewModel.onUIEvent(
                            ChangePasswordViewModel.UIEvent.OnNewPasswordValueChange(
                                it
                            )
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(onNext = {
                        focusManager.clearFocus()
                    }),
                    labelText = stringResource(id = R.string.profile_settings_new_password),
                    isPassword = true,
                    modifier = Modifier
                        .padding(top = 24.dp),
                    isRequired = true,
                    isRequiredMessage = stringResource(id = R.string.sign_up_password_required),
                    isError = viewModel.uiState.newPasswordError.first,
                    errorMessage = if (viewModel.uiState.newPasswordError.first) {
                        stringResource(id = viewModel.uiState.newPasswordError.second)
                    } else {
                        null
                    }
                )

                CustomOutlinedTextField(
                    value = viewModel.uiState.newPasswordConfirmation,
                    onValueChange = {
                        viewModel.onUIEvent(
                            ChangePasswordViewModel.UIEvent.OnNewPasswordConfirmationValueChange(
                                it
                            )
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(onNext = {
                        focusManager.clearFocus()
                    }),
                    labelText = stringResource(id = R.string.profile_settings_confirm_new_password),
                    isPassword = true,
                    modifier = Modifier
                        .padding(top = 24.dp),
                    isRequired = true,
                    isRequiredMessage = stringResource(id = R.string.sign_up_password_required),
                    isError = viewModel.uiState.newPasswordConfirmationError.first,
                    errorMessage = if (viewModel.uiState.newPasswordConfirmationError.first) {
                        stringResource(id = viewModel.uiState.newPasswordConfirmationError.second)
                    } else {
                        null
                    }
                )
                FlowRow(
                    Modifier
                        .padding(top = 8.dp)
                ) {
                    PasswordRequirementLabels(
                        text = stringResource(id = R.string.sign_up_password_requirement_eight_characters_minimum),
                        state = viewModel.uiState.eightCharactersMinimumState
                    )
                    PasswordRequirementLabels(
                        text = stringResource(id = R.string.sign_up_password_requirement_one_uppercase),
                        state = viewModel.uiState.oneUppercaseState
                    )
                    PasswordRequirementLabels(
                        text = stringResource(id = R.string.sign_up_password_requirement_one_lowercase),
                        state = viewModel.uiState.oneLowercaseState
                    )
                    PasswordRequirementLabels(
                        text = stringResource(id = R.string.sign_up_password_requirement_one_number),
                        state = viewModel.uiState.oneNumberState
                    )
                    PasswordRequirementLabels(
                        text = stringResource(id = R.string.sign_up_password_requirement_one_characer),
                        state = viewModel.uiState.oneCharacterState
                    )
                }
            }

            CustomButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(horizontal = 16.dp)
                    .constrainAs(changePasswordButton) {
                        bottom.linkTo(parent.bottom, margin = 40.dp)
                    },
                buttonType = CustomButtonType.PrimaryPrimary,
                text = stringResource(id = R.string.profile_change_email_button),
                enable = viewModel.uiState.isButtonEnabled,
                onClick = {
                    viewModel.onUIEvent(ChangePasswordViewModel.UIEvent.OnUpdatePassword)
                }
            )
        }
    }
}