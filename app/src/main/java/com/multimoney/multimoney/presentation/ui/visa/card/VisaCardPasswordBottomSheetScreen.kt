package com.multimoney.multimoney.presentation.ui.visa.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.R
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnHidePasswordBottomSheet
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnPasswordChange
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnPasswordConfirmClick
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnPasswordForgotPassword
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardViewModel.UIEvent.OnTryWithPassword
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomInformativeChip
import com.multimoney.multimoney.presentation.uielement.CustomModalBottomSheet
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.uielement.Size.Large
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun VisaCardPasswordBottomSheetScreen(
    viewModel: VisaCardViewModel,
    coroutineScope: CoroutineScope,
    modalBottomSheetState: ModalBottomSheetState
) {
    CustomModalBottomSheet(
        title = viewModel.uiState.passwordTitle,
        closeIcon = R.drawable.ic_close_bottom_sheet,
        closeIconVisible = viewModel.uiState.isPasswordMessage.not(),
        closeAction = { viewModel.onUIEvent(OnHidePasswordBottomSheet) },
        modalBottomSheetState = modalBottomSheetState,
        coroutineScope = coroutineScope
    ) {
        if (viewModel.uiState.isPasswordMessage) {
            VisaCardPasswordMessageContent(
                onCancelClick = { viewModel.onUIEvent(OnHidePasswordBottomSheet) },
                onTryWithPassword = { viewModel.onUIEvent(OnTryWithPassword) }
            )
        } else {
            VisaCardPasswordContent(
                onPasswordConfirmClick = { viewModel.onUIEvent(OnPasswordConfirmClick) },
                onPasswordForgotPassword = { viewModel.onUIEvent(OnPasswordForgotPassword) },
                onPasswordChange = { value -> viewModel.onUIEvent(OnPasswordChange(value)) },
                passwordValue = viewModel.uiState.password,
                passwordError = viewModel.uiState.passwordError,
                isPasswordConfirmButtonEnabled = viewModel.uiState.isPasswordConfirmButtonEnabled
            )
        }
    }
}

@Composable
@Preview
fun VisaCardPasswordMessageContent(
    onCancelClick: () -> Unit = {},
    onTryWithPassword: () -> Unit = {}
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp)
    ) {
        CustomInformativeChip(
            text = stringResource(id = string.visa_card_password_message_subtitle),
            textStyle = Typography.caption.copy(color = MultimoneyTheme.colors.textAlertColor),
            modifier = Modifier.padding(top = 16.dp),
            startIcon = R.drawable.ic_information,
            startIconTint = MultimoneyTheme.colors.textAlertColor,
            size = Large
        )
        Row(modifier = Modifier.padding(top = 40.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            ClickableText(
                text = AnnotatedString(stringResource(id = string.accept)),
                style = Typography.subtitle2.copy(color = MultimoneyTheme.colors.textInformation),
                modifier = Modifier
                    .wrapContentSize()
                    .padding(start = 4.dp),
                onClick = {
                    onCancelClick()
                }
            )
            ClickableText(
                text = AnnotatedString(stringResource(id = string.visa_card_password_message_try_with_password)),
                style = Typography.subtitle2.copy(color = MultimoneyTheme.colors.textInformation),
                modifier = Modifier
                    .wrapContentSize()
                    .padding(start = 4.dp),
                onClick = {
                    onTryWithPassword()
                }
            )
        }
    }
}

@Composable
@Preview
fun VisaCardPasswordContent(
    passwordValue: String = "",
    passwordError: Pair<Boolean, Int> = Pair(false, R.string.empty),
    onPasswordConfirmClick: () -> Unit = {},
    onPasswordForgotPassword: () -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    isPasswordConfirmButtonEnabled: Boolean = false
) {
    // Properties
    val focusManager = LocalFocusManager.current
    Column(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp)
    ) {
        CustomOutlinedTextField(
            value = passwordValue,
            onValueChange = { onPasswordChange(it) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone =
                {
                    focusManager.clearFocus()
                }
            ),
            labelText = stringResource(id = string.sign_in_label_password),
            isPassword = true,
            modifier = Modifier.fillMaxWidth(),
            isRequired = true,
            isRequiredMessage = stringResource(id = string.sign_in_password_required),
            isError = passwordError.first,
            errorMessage = if (passwordError.first) {
                stringResource(id = passwordError.second)
            } else {
                null
            }
        )
        ClickableText(
            text = AnnotatedString(stringResource(id = R.string.sign_in_forgot_password)),
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 24.dp),
            style = Typography.button.copy(
                textDecoration = TextDecoration.Underline,
                color = MultimoneyTheme.colors.textLink
            ),
            onClick = { onPasswordForgotPassword() }
        )
        CustomButton(
            onClick = {
                onPasswordConfirmClick()
            },
            text = stringResource(id = string.visa_card_password_button),
            modifier = Modifier
                .padding(top = 40.dp)
                .fillMaxWidth()
                .height(56.dp),
            enable = isPasswordConfirmButtonEnabled
        )
    }
}
