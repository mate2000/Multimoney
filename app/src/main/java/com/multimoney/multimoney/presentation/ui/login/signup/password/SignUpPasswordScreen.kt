package com.multimoney.multimoney.presentation.ui.login.signup.password

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.uielement.CustomPasswordRequirementLabel

@Composable
@Preview
fun SignUpPasswordScreen(
    viewModel: SignUpPasswordViewModel = hiltViewModel(),
    sharedViewModel: SignUpViewModel = hiltViewModel()
) {

    // Properties
    val focusManager = LocalFocusManager.current

    Column(Modifier.padding(16.dp)) {
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = Typography.h6.toSpanStyle()
                        .copy(fontWeight = FontWeight.SemiBold)
                ) {
                    append(stringResource(id = R.string.sign_up_password_title))
                }
            },
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )
        CustomOutlinedTextField(
            value = viewModel.password,
            onValueChange = {
                viewModel.apply {
                    viewModel.password = it
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.clearFocus()
            }),
            labelText = stringResource(id = R.string.sign_up_label_password),
            isPassword = true,
            modifier = Modifier
                .padding(top = 24.dp),
            isRequired = true,
            isRequiredMessage = stringResource(id = R.string.sign_up_password_required),
            isError = viewModel.passwordError.first,
            errorMessage = if (viewModel.passwordError.first) {
                stringResource(id = viewModel.passwordError.second)
            } else {
                null
            }
        )
        CustomOutlinedTextField(
            value = viewModel.confirmPassword,
            onValueChange = {
                viewModel.apply {
                    viewModel.confirmPassword = it
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            }),
            labelText = stringResource(id = R.string.sign_up_label_confirm_password),
            isPassword = true,
            modifier = Modifier
                .padding(top = 16.dp),
            isRequired = true,
            isRequiredMessage = stringResource(id = R.string.sign_up_password_required),
            isError = viewModel.confirmPasswordError.first,
            errorMessage = if (viewModel.confirmPasswordError.first) {
                stringResource(id = viewModel.confirmPasswordError.second)
            } else {
                null
            }
        )
        Row(Modifier.padding(top = 8.dp)) {
            PasswordRequirementLabels(
                text = stringResource(id = R.string.sign_up_password_requirement_eight_characters_minimum),
                isError = viewModel.eightCharactersMinimum
            )
            PasswordRequirementLabels(
                text = stringResource(id = R.string.sign_up_password_requirement_one_uppercase),
                isError = viewModel.oneUppercaseError
            )
        }
        Row {
            PasswordRequirementLabels(
                text = stringResource(id = R.string.sign_up_password_requirement_one_lowercase),
                isError = viewModel.oneLowercaseError
            )
            PasswordRequirementLabels(
                text = stringResource(id = R.string.sign_up_password_requirement_one_number),
                isError = viewModel.oneNumberError
            )
        }
    }
}

@Composable
fun PasswordRequirementLabels(modifier: Modifier = Modifier, text: String, isError: Boolean?) {
    CustomPasswordRequirementLabel(
        modifier,
        text = text,
        successIcon = R.drawable.ic_check,
        errorIcon = R.drawable.ic_close,
        isError = isError
    )
}