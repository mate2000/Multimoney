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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel
import com.multimoney.multimoney.presentation.ui.trasnformation.formatId
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.uielement.CustomRadioButton
import com.multimoney.multimoney.presentation.util.validId

@Composable
@Preview
fun SignUpPersonalDataCrScreen(
    sharedViewModel: SignUpViewModel = hiltViewModel(),
    viewModel: SignUpPersonalDataViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
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
                viewModel.crPersonalDocument = CrDocuments.IdDocument.document
            },
            radioModifier = Modifier.padding(0.dp),
            text = CrDocuments.IdDocument.document,
            selected = viewModel.crPersonalDocument == CrDocuments.IdDocument.document,
            onOptionSelected = { viewModel.crPersonalDocument = CrDocuments.IdDocument.document }
        )
        CustomRadioButton(
            modifier = customRadioModifier.clickable {
                viewModel.crPersonalDocument = CrDocuments.Dimex.document
            },
            radioModifier = Modifier.padding(0.dp),
            selected = viewModel.crPersonalDocument == CrDocuments.Dimex.document,
            text = CrDocuments.Dimex.document,
            onOptionSelected = { viewModel.crPersonalDocument = CrDocuments.Dimex.document }
        )
        CustomOutlinedTextField(
            value = viewModel.personalDocumentValue,
            placeHolder = stringResource(id = if (viewModel.crPersonalDocument == CrDocuments.IdDocument.document) R.string.sign_up_cr_id_hint else R.string.sign_up_cr_dimex_hint),
            onValueChange = { newString ->
                viewModel.crFilterDocument(newString)
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
            isError = viewModel.personalIdError.first,
            customTransformation = if (viewModel.crPersonalDocument == CrDocuments.IdDocument.document) formatId() else null,
            onDebounceValidation = {
                viewModel.personalIdError = validId(
                    if (viewModel.crPersonalDocument == CrDocuments.IdDocument.document) Nationalities.CostaRicaId.documentSize else Nationalities.CostaRicaDimex.documentSize,
                    R.string.sign_up_id_not_valid,
                    viewModel.personalDocumentValue.length
                )
                sharedViewModel.isContinueEnabled = viewModel.validateFields()
            }
        )
    }
}