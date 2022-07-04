package com.multimoney.multimoney.presentation.ui.login.signup.personaldata

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnContinueEnable
import com.multimoney.multimoney.presentation.uielement.CustomImage
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.util.CrDocuments
import com.multimoney.multimoney.presentation.util.transformation.formatId

@Composable
@Preview
fun SignUpPersonalDataCrScreen(
    sharedViewModel: SignUpViewModel = hiltViewModel(),
    viewModel: SignUpPersonalDataViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    Column(
        Modifier
            .wrapContentSize()
            .padding(top = 16.dp)
    ) {
        CustomOutlinedTextField(
            value = viewModel.uiState.personalDocumentValue,
            placeHolder = stringResource(id = if (viewModel.crPersonalDocument == CrDocuments.IdDocument.document) R.string.sign_up_personal_data_cr_id_hint else R.string.sign_up_personal_data_cr_dimex_hint),
            onValueChange = { newString ->
                viewModel.crFilterDocument(newString)
                sharedViewModel.userData?.identification = viewModel.uiState.personalDocumentValue
                sharedViewModel.onUIEvent(OnContinueEnable(viewModel.validateFields()))
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            }),
            labelText = stringResource(id = R.string.sign_up_personal_data_document_cr),
            modifier = Modifier.padding(top = 44.dp),
            isRequired = true,
            isRequiredMessage = stringResource(id = R.string.sign_up_personal_data_id_required),
            isError = viewModel.personalIdError.first,
            errorMessage = stringResource(id = viewModel.personalIdError.second),
            customTransformation = if (viewModel.crPersonalDocument == CrDocuments.IdDocument.document) formatId() else null,
            onDebounceValidation = {
                viewModel.validateCrDocument(sharedViewModel.userData?.email ?: "")
                sharedViewModel.onUIEvent(OnContinueEnable(viewModel.validateFields()))
            }
        )

        if (viewModel.isLoading) {
            sharedViewModel.userData?.fullName = null
            Row(modifier = Modifier.padding(top = 12.dp)) {
                CustomImage(
                    drawableResource = R.drawable.ic_information,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Text(
                    text = stringResource(id = R.string.sign_up_personal_data_cr_loading_data),
                    style = Typography.subtitle2.copy(color = MultimoneyTheme.colors.textInformation),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 9.dp)
                )
            }
        }

        if (viewModel.onSuccessDataInformationClient?.fullName.isNullOrBlank().not()) {
            viewModel.onSuccessDataInformationClient?.apply {
                sharedViewModel.onUIEvent(OnContinueEnable(viewModel.validateFields()))
                sharedViewModel.userData?.fullName = fullName
            }
            Row(modifier = Modifier.padding(top = 16.dp, start = 4.dp)) {
                CustomImage(
                    drawableResource = R.drawable.ic_check,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = stringResource(id = R.string.sing_up_personal_data_cr_complete_name),
                    style = Typography.body2.copy(
                        color = MultimoneyTheme.colors.textSubhead
                    )
                )
            }
            Text(
                modifier = Modifier.padding(top = 8.dp, start = 4.dp),
                text = viewModel.onSuccessDataInformationClient?.fullName.toString(),
                style = Typography.body2.copy(color = MultimoneyTheme.colors.text)
            )
        }
    }

    if (viewModel.closeKeyboard) {
        focusManager.clearFocus()
        viewModel.closeKeyboard = false
    }
}