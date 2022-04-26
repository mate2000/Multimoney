package com.multimoney.multimoney.presentation.ui.personal

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringArrayResource
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
import com.multimoney.multimoney.presentation.uielement.CustomRadioButton

@Composable
@Preview
fun SignUpPersonalDataCrScreen(
    sharedViewModel: SignUpViewModel = hiltViewModel(),
    viewModel: SignUpPersonalDataViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    val documents = stringArrayResource(id = R.array.sign_up_costa_rica_documents)
    val customRadioModifier = Modifier
        .wrapContentSize()
        .selectable(
            selected = true,
            onClick = {}
        )
    Column(
        Modifier
            .wrapContentSize()
            .padding(top = 16.dp)
    ) {
        CustomRadioButton(
            modifier = customRadioModifier.clickable {
                viewModel.crPersonalDocument = documents[0]
            },
            radioModifier = Modifier.padding(0.dp),
            text = documents[0],
            selected = viewModel.crPersonalDocument == documents[0],
            onOptionSelected = { viewModel.crPersonalDocument = documents[0] }
        )
        CustomRadioButton(
            modifier = customRadioModifier.clickable {
                viewModel.crPersonalDocument = documents[1]
            },
            radioModifier = Modifier.padding(0.dp),
            selected = viewModel.crPersonalDocument == documents[1],
            text = documents[1],
            onOptionSelected = { viewModel.crPersonalDocument = documents[1] }
        )
        CustomOutlinedTextField(
            value = viewModel.personalDocumentValue,
            placeHolder = stringResource(id = if (viewModel.crPersonalDocument == documents[0]) R.string.sign_up_cr_id_hint else R.string.sign_up_cr_dimex_hint),
            onValueChange = { newString ->
                if (viewModel.crPersonalDocument == documents[0] && newString.length <= ID_LENGTH) {
                    viewModel.personalDocumentValue = newString.filter { it.isDigit() }
                } else if (viewModel.crPersonalDocument == documents[1] && newString.length <= DIMEX_LENGTH) {
                    viewModel.personalDocumentValue = newString.filter { it.isDigit() }
                }
                sharedViewModel.isContinueEnabled = viewModel.validateFields()
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            }),
            labelText = stringResource(id = R.string.sign_up_document_cr),
            leadingIcon = R.drawable.ic_identification,
            modifier = Modifier.padding(top = 44.dp),
            isRequired = true,
            isRequiredMessage = stringResource(id = R.string.sign_up_id_required),
            isError = viewModel.personalIDError.first,
            customTransformation = if (viewModel.crPersonalDocument == documents[0]) formatId() else null,
            onDebounceValidation = {
                viewModel.validId(
                    if (viewModel.crPersonalDocument == documents[0]) ID_LENGTH else DIMEX_LENGTH,
                    R.string.sign_up_id_not_valid
                )
            }
        )
    }
}
const val ID_LENGTH = 9
const val DIMEX_LENGTH = 12

fun formatId(): VisualTransformation =
    object : VisualTransformation {
        override fun filter(text: AnnotatedString): TransformedText {
            val offset = object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int {
                    if (offset <= 1) return offset
                    if (offset <= 4) return offset + 1
                    if (offset <= 15) return offset + 2
                    return 11
                }

                override fun transformedToOriginal(offset: Int): Int {
                    if (offset <= 0) return offset
                    if (offset <= 3) return offset - 1
                    if (offset <= 14) return offset - 2
                    return 10
                }
            }
            var formattedText = ""
            val trimmed = if (text.text.length >= 15) text.text.substring(0..14) else text.text

            for (i in trimmed.indices) {
                formattedText += trimmed[i]
                if (i == 0 || i == 4) formattedText += " "
            }
            return TransformedText(AnnotatedString(formattedText), offset)
        }
    }