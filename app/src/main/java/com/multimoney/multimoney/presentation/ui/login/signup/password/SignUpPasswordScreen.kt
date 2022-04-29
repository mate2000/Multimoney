package com.multimoney.multimoney.presentation.ui.login.signup.password

import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.uielement.CustomPasswordRequirementLabel

@Composable
fun SignUpPasswordScreen(
    viewModel: SignUpPasswordViewModel = hiltViewModel(),
    sharedViewModel: SignUpViewModel = hiltViewModel()
) {

    // Properties
    val focusManager = LocalFocusManager.current

    Column {
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = Typography.h4.toSpanStyle()
                        .copy(fontWeight = FontWeight.SemiBold)
                ) {
                    append(stringResource(id = R.string.sign_up_password_title))
                }
            },
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )
        CustomOutlinedTextField(
            value = viewModel.userPassword,
            onValueChange = {
                viewModel.apply {
                    viewModel.userPassword = it
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
                .padding(top = 16.dp),
            isRequired = true,
            isRequiredMessage = stringResource(id = R.string.sign_up_password_required),
            isError = viewModel.userPasswordError.first,
            errorMessage = if (viewModel.userPasswordError.first) {
                stringResource(id = viewModel.userPasswordError.second)
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
        PasswordRequirementLabels(
            text = stringResource(id = R.string.sign_up_password_requirement_eight_characters_minimum),
            isError = viewModel.eightCharactersMinimum
        )
        PasswordRequirementLabels(
            text = stringResource(id = R.string.sign_up_password_requirement_eight_characters_minimum),
            isError = viewModel.oneUppercase
        )
        PasswordRequirementLabels(
            text = stringResource(id = R.string.sign_up_password_requirement_eight_characters_minimum),
            isError = viewModel.oneLowercase
        )
        PasswordRequirementLabels(
            text = stringResource(id = R.string.sign_up_password_requirement_eight_characters_minimum),
            isError = viewModel.oneNumber
        )
    }
}

@Composable
fun PasswordRequirementLabels(text: String, isError: Boolean?) {
    CustomPasswordRequirementLabel(
        text = text,
        successIcon = R.drawable.ic_check,
        errorIcon = R.drawable.ic_close,
        isError = isError
    )
}