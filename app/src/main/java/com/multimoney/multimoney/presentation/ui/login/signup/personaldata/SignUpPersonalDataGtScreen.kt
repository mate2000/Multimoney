package com.multimoney.multimoney.presentation.ui.login.signup.personaldata

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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.util.Nationalities
import com.multimoney.multimoney.presentation.util.transformation.formatDpi
import com.multimoney.multimoney.presentation.util.validId

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
            placeHolder = stringResource(id = R.string.sign_up_personal_data_gt_id_hint),
            onValueChange = { newString ->
                if (newString.length <= Nationalities.Guatemala.documentSize) {
                    viewModel.personalDocumentValue = newString.filter { it.isDigit() }
                    sharedViewModel.userData?.identification = viewModel.personalDocumentValue
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            }),
            labelText = stringResource(id = R.string.sign_up_personal_data_document_gt),
            leadingIcon = R.drawable.ic_identification,
            modifier = Modifier
                .padding(top = 44.dp),
            isRequired = true,
            isRequiredMessage = stringResource(id = R.string.sign_up_personal_data_gt_id_required),
            isError = viewModel.personalIdError.first,
            errorMessage = stringResource(id = viewModel.personalIdError.second),
            customTransformation = formatDpi(),
            onDebounceValidation = {
                viewModel.personalIdError = validId(
                    Nationalities.Guatemala.documentSize,
                    R.string.sign_up_personal_data_dpi_gt_not_valid,
                    viewModel.personalDocumentValue.length
                )
                sharedViewModel.isContinueEnabled = viewModel.validateFields()
            }
        )
        CustomOutlinedTextField(
            value = viewModel.nameValue,
            onValueChange = {
                viewModel.nameValue = it
                sharedViewModel.apply {
                    userData?.firstName = it
                    userData?.fullName = "$it ${userData?.lastName}"
                    isContinueEnabled = viewModel.validateFields()
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            }),
            labelText = stringResource(id = R.string.sign_up_personal_data_names),
            modifier = Modifier
                .padding(top = 44.dp),
            isRequired = true,
            placeHolder = stringResource(id = R.string.sign_up_personal_data_name_hint),
            isRequiredMessage = stringResource(id = R.string.sign_up_personal_data_name_error),
            isError = viewModel.nameError.first
        )
        CustomOutlinedTextField(
            placeHolder = stringResource(id = R.string.sign_up_personal_data_lastname_hint),
            value = viewModel.lastNameValue,
            onValueChange = {
                viewModel.lastNameValue = it
                sharedViewModel.apply {
                    userData?.lastName = it
                    userData?.fullName = "${userData?.firstName} $it"
                    isContinueEnabled = viewModel.validateFields()
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            }),
            labelText = stringResource(id = R.string.sign_up_personal_data_lastname),
            modifier = Modifier
                .padding(top = 44.dp),
            isRequired = true,
            isRequiredMessage = stringResource(id = R.string.sign_up_personal_data_lastname_error),
            isError = viewModel.lastNameError.first
        )
    }
}