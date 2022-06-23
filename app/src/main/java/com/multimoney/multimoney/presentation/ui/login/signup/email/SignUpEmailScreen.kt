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
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.SignUpStep
import com.multimoney.data.util.catalog.UserStatus
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel
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
    LaunchedEffect(true) {
        viewModel.isFirstLaunch = true
        sharedViewModel.isContinueEnabled = viewModel.isFormValid()
        sharedViewModel.nextAction = {
            viewModel.apply {
                if (isDataChanged()) {
                    callMutationUserValidationUseCase(
                        userEmail,
                        SignUpStep.One.name,
                        Brand.Revamp.id
                    )
                } else {
                    sharedViewModel.nextStep()
                }
            }
        }
    }

    LaunchedEffect(viewModel.isLoading) {
        if (viewModel.isFirstLaunch.not()) {
            sharedViewModel.isLoading = viewModel.isLoading
        }
    }

    LaunchedEffect(viewModel.onSuccessUserDataValidation) {
        if (viewModel.isFirstLaunch.not()) {
            sharedViewModel.userData = viewModel.onSuccessUserDataValidation
            if (sharedViewModel.userData?.userStatus == UserStatus.Incomplete.name) {
                sharedViewModel.nextStep()
            } else if (sharedViewModel.userData?.userStatus == UserStatus.Active.name) {
                sharedViewModel.openDialog = DialogParameters(
                    title = R.string.sign_up_email_user_completed_dialog_title,
                    description = viewModel.userCompletedDialogDescription,
                    positiveText = R.string.sign_up_email_user_completed_dialog_positive,
                    positiveAction = { sharedViewModel.previousStep() },
                    isActive = mutableStateOf(true)
                )
            }
        }
    }

    LaunchedEffect(viewModel.onFailure) {
        if (viewModel.isFirstLaunch.not()) {
            sharedViewModel.openDialog = viewModel.onFailure
        }
    }

    viewModel.apply {
        isFirstLaunch = false
        userCompletedDialogDescription =
            stringResource(id = R.string.sign_up_email_user_completed_dialog_description)
    }

    Column(modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp)) {
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = Typography.h4.toSpanStyle()
                        .copy(
                            color = MultimoneyTheme.colors.text,
                            fontWeight = FontWeight.SemiBold
                        )
                ) {
                    append(stringResource(id = R.string.sign_up_email_title))
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
                    clearUserEmailError()
                    sharedViewModel.isContinueEnabled = isFormValid()
                }
            },
            onDebounceValidation = { viewModel.isUserEmailValid() },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            }),
            labelText = stringResource(id = R.string.label_email),
            placeHolder = stringResource(id = R.string.sign_up_email_placeholder),
            modifier = Modifier
                .padding(top = 24.dp),
            isRequired = true,
            isRequiredMessage = stringResource(id = R.string.sign_up_email_required),
            isError = viewModel.userEmailError.first,
            errorMessage = stringResource(id = viewModel.userEmailError.second)
        )
    }
}