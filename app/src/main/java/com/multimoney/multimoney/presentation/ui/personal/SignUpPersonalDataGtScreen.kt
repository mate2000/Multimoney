package com.multimoney.multimoney.presentation.ui.personal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField

@Composable
@Preview
fun SignUpPersonalDataGtScreen(
    sharedViewModel: SignUpViewModel = hiltViewModel(),
    viewModel: SignUpPersonalDataViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    Column(
        Modifier.fillMaxSize()
    ) {
        CustomOutlinedTextField(
            value = viewModel.personalDocumentValue,
            placeHolder = stringResource(id = R.string.sing_up_gt_id_hint),
            onValueChange = { newString ->
                if (newString.length <= 13) {
                    viewModel.personalDocumentValue = newString.filter { it.isDigit() }
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            }),
            labelText = stringResource(id = R.string.sign_up_document_gt),
            leadingIcon = R.drawable.ic_identification,
            modifier = Modifier
                .padding(top = 44.dp),
            isRequired = true,
            isRequiredMessage = stringResource(id = R.string.sign_up_dpi_required),
            isError = viewModel.personalIDError.first,
            customTransformation = formatDpi(),
            onDebounceValidation = {
                sharedViewModel.isContinueEnabled =
                    viewModel.validId(13, R.string.sign_up_dpi_not_valid)
            }
        )
        CustomOutlinedTextField(
            value = viewModel.nameValue,
            onValueChange = {
                viewModel.nameValue = it
                sharedViewModel.isContinueEnabled = viewModel.validateFields()
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            }),
            labelText = stringResource(id = R.string.sign_up_names),
            modifier = Modifier
                .padding(top = 44.dp),
            isRequired = true,
            placeHolder = stringResource(id = R.string.sing_up_name_hint),
            isRequiredMessage = stringResource(id = R.string.sign_up_name_error),
            isError = viewModel.nameError.first
        )
        CustomOutlinedTextField(
            placeHolder = stringResource(id = R.string.sing_up_lastname_hint),
            value = viewModel.lastNameValue,
            onValueChange = {
                viewModel.lastNameValue = it
                sharedViewModel.isContinueEnabled = viewModel.validateFields()
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            }),
            labelText = stringResource(id = R.string.sign_up_lastname),
            modifier = Modifier
                .padding(top = 44.dp),
            isRequired = true,
            isRequiredMessage = stringResource(id = R.string.sign_up_lastname_error),
            isError = viewModel.lastNameError.first
        )
    }
}

fun formatDpi(): VisualTransformation =
    object : VisualTransformation {
        override fun filter(text: AnnotatedString): TransformedText {
            val offset = object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int {
                    if (offset <= 4) return offset
                    if (offset <= 9) return offset + 1
                    if (offset <= 15) return offset + 2
                    return 15
                }

                override fun transformedToOriginal(offset: Int): Int {
                    if (offset <= 3) return offset
                    if (offset <= 10) return offset - 1
                    if (offset <= 14) return offset - 2
                    return 14
                }
            }
            var formattedText = ""
            val trimmed = if (text.text.length >= 15) text.text.substring(0..14) else text.text

            for (i in trimmed.indices) {
                formattedText += trimmed[i]
                if (i == 3 || i == 8) formattedText += " "
            }
            return TransformedText(AnnotatedString(formattedText), offset)
        }
    }