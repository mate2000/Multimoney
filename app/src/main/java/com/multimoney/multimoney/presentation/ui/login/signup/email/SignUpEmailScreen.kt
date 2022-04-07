package com.multimoney.multimoney.presentation.ui.login.signup.email

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

@Composable
@Preview
fun SignUpEmailScreen(
    viewModel: SignUpEmailViewModel = hiltViewModel(),
    sharedViewModel: SignUpViewModel = hiltViewModel()
) {

    // Properties
    val focusManager = LocalFocusManager.current
    LaunchedEffect(true) {
        sharedViewModel.isContinueEnabled = viewModel.isFormValid()
    }

    Column(modifier = Modifier.padding(vertical = 32.dp, horizontal = 16.dp)) {
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = Typography.h4.toSpanStyle()
                        .copy(fontWeight = FontWeight.SemiBold)
                ) {
                    append(stringResource(id = R.string.sign_up_email_welcome))
                }
            },
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )

        // Fields
        CustomOutlinedTextField(
            value = viewModel.userEmail,
            onValueChange = {
                viewModel.apply {
                    userEmail = it
                    sharedViewModel.isContinueEnabled = isFormValid()
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            }),
            labelText = stringResource(id = R.string.label_email),
            leadingIcon = R.drawable.ic_envelope,
            modifier = Modifier
                .padding(top = 24.dp),
            isRequired = true,
            isRequiredMessage = stringResource(id = R.string.sign_up_email_required),
            isError = viewModel.userEmailError.first,
            errorMessage = stringResource(id = viewModel.userEmailError.second)
        )
    }
}