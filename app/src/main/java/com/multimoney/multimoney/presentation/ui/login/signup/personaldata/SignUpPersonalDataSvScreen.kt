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
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnContinueValueChange
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.util.Nationalities
import com.multimoney.multimoney.presentation.util.transformation.formatDui
import com.multimoney.multimoney.presentation.util.validDui

@Composable
@Preview
fun SignUpPersonalDataSvScreen(
    sharedViewModel: SignUpViewModel = hiltViewModel(),
    viewModel: SignUpPersonalDataViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    Column(
        Modifier
            .fillMaxSize()
    ) {
        CustomOutlinedTextField(
            value = viewModel.personalDocumentValue,
            placeHolder = stringResource(id = R.string.sign_up_personal_data_sv_id_hint),
            onValueChange = { text ->
                if (text.length <= Nationalities.ElSalvador.documentSize) {
                    viewModel.personalDocumentValue = text.filter { it.isDigit() }
                    sharedViewModel.userData?.identification = viewModel.personalDocumentValue
                }
            },
            onDebounceValidation = {
                viewModel.personalIdError = validDui(viewModel.personalDocumentValue)
                sharedViewModel.onUIEvent(OnContinueValueChange(viewModel.validateFields()))
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            }),
            labelText = stringResource(id = R.string.sign_up_personal_data_document_sv),
            leadingIcon = R.drawable.ic_identification,
            modifier = Modifier
                .padding(top = 44.dp),
            isRequired = true,
            isRequiredMessage = stringResource(id = R.string.sign_up_personal_data_id_sv_required),
            isError = viewModel.personalIdError.first,
            errorMessage = stringResource(id = viewModel.personalIdError.second),
            customTransformation = formatDui()
        )
        CustomOutlinedTextField(
            placeHolder = stringResource(id = R.string.sign_up_personal_data_name_hint),
            value = viewModel.nameValue,
            onValueChange = {
                viewModel.nameValue = it
                sharedViewModel.apply {
                    userData?.firstName = it
                    userData?.fullName = "$it ${userData?.lastName}"
                    sharedViewModel.onUIEvent(OnContinueValueChange(viewModel.validateFields()))
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
            modifier = Modifier.padding(top = 44.dp),
            isRequired = true,
            isRequiredMessage = stringResource(id = R.string.sign_up_personal_data_name_error),
            isError = viewModel.nameError.first,
        )

        CustomOutlinedTextField(
            placeHolder = stringResource(id = R.string.sign_up_personal_data_lastname_hint),
            value = viewModel.lastNameValue,
            onValueChange = {
                viewModel.lastNameValue = it
                sharedViewModel.apply {
                    userData?.lastName = it
                    userData?.fullName = "${userData?.firstName} $it"
                    sharedViewModel.onUIEvent(OnContinueValueChange(viewModel.validateFields()))
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
