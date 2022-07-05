package com.multimoney.multimoney.presentation.ui.login.signup.personaldata

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.R.array
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnContinueEnable
import com.multimoney.multimoney.presentation.ui.login.signup.personaldata.SignUpPersonalDataViewModel.UIEvent.OnFirstNameChange
import com.multimoney.multimoney.presentation.uielement.CustomDropdown
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
        CustomDropdown(
            modifier = Modifier
                .wrapContentSize(Alignment.TopStart)
                .focusable(false)
                .padding(top = 16.dp),
            items = stringArrayResource(id = array.sign_up_personal_sv_documents).sorted(),
            onValueChange = {

            },
            labelText = stringResource(id = R.string.sign_up_personal_data_document_label),
            value = viewModel.uiState.personalDocumentValue,
            placeHolder = stringResource(id = R.string.sign_up_personal_data_document_hint)
        )
        CustomOutlinedTextField(
            value = viewModel.uiState.personalDocumentValue,
            placeHolder = stringResource(id = R.string.sign_up_personal_data_sv_id_hint),
            onValueChange = { text ->
                if (text.length <= Nationalities.ElSalvador.documentSize) {
                    viewModel.onUIEvent(OnFirstNameChange(text))
                    sharedViewModel.userData?.identification =
                        viewModel.uiState.personalDocumentValue
                }
            },
            onDebounceValidation = {
                viewModel.personalIdError = validDui(viewModel.uiState.personalDocumentValue)
                sharedViewModel.onUIEvent(OnContinueEnable(viewModel.validateFields()))
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            }),
            labelText = stringResource(id = R.string.sign_up_personal_data_document_sv),
            modifier = Modifier
                .padding(top = 44.dp),
            isRequired = true,
            isRequiredMessage = stringResource(id = R.string.sign_up_personal_data_id_sv_required),
            isError = viewModel.personalIdError.first,
            errorMessage = stringResource(id = viewModel.personalIdError.second),
            customTransformation = formatDui()
        )
        CustomOutlinedTextField(
            placeHolder = stringResource(id = R.string.sign_up_personal_data_first_lastname_hint),
            value = viewModel.uiState.firstNameValue,
            onValueChange = {
                //viewModel.nameValue = it
                sharedViewModel.apply {
                    userData?.firstName = it
                    userData?.fullName = "$it ${userData?.firstLastName}"
                    sharedViewModel.onUIEvent(OnContinueEnable(viewModel.validateFields()))
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
            value = viewModel.uiState.firstLastNameValue,
            onValueChange = {
                //viewModel.lastNameValue = it
                sharedViewModel.apply {
                    userData?.firstLastName = it
                    userData?.fullName = "${userData?.firstName} $it"
                    sharedViewModel.onUIEvent(OnContinueEnable(viewModel.validateFields()))
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
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 44.dp)
        ) {
            CustomOutlinedTextField(
                placeHolder = stringResource(id = R.string.sign_up_personal_data_first_name_hint),
                value = viewModel.uiState.nameValue,
                onValueChange = {
                    //viewModel.nameValue = it
                    sharedViewModel.apply {
                        userData?.firstName = it
                        userData?.fullName = "$it ${userData?.firstName}"
                        sharedViewModel.onUIEvent(OnContinueEnable(viewModel.validateFields()))
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
                    .weight(0.5f)
                    .padding(end = 4.dp),
                isRequired = true,
                isRequiredMessage = stringResource(id = R.string.sign_up_personal_data_name_error),
                isError = viewModel.nameError.first,
            )

            CustomOutlinedTextField(
                placeHolder = stringResource(id = R.string.sign_up_personal_data_second_name_hint),
                value = viewModel.uiState.secondNameValue,
                onValueChange = {
                    //viewModel.nameValue = it
                    sharedViewModel.apply {
                        userData?.firstName = it
                        userData?.fullName = "$it ${userData?.secondName}"
                        sharedViewModel.onUIEvent(OnContinueEnable(viewModel.validateFields()))
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                labelText = stringResource(id = R.string.error_empty),
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }),
                modifier = Modifier
                    .weight(0.5f)
                    .padding(start = 4.dp),
                isError = viewModel.nameError.first,
            )
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 44.dp)
        ) {
            CustomOutlinedTextField(
                placeHolder = stringResource(id = R.string.sign_up_personal_data_first_lastname_hint),
                value = viewModel.uiState.lastNameValue,
                onValueChange = {
                    //viewModel.lastNameValue = it
                    sharedViewModel.apply {
                        userData?.firstLastName = it
                        userData?.fullName = "${userData?.firstName} $it"
                        sharedViewModel.onUIEvent(OnContinueEnable(viewModel.validateFields()))
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
                isError = viewModel.nameError.first,
            )

            CustomOutlinedTextField(
                placeHolder = stringResource(id = R.string.sign_up_personal_data_second_lastname_hint),
                value = viewModel.uiState.secondLastNameValue,
                onValueChange = {
                    //viewModel.nameValue = it
                    sharedViewModel.apply {
                        userData?.firstName = it
                        userData?.fullName = "$it ${userData?.secondLastName}"
                        sharedViewModel.onUIEvent(OnContinueEnable(viewModel.validateFields()))
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                labelText = stringResource(id = R.string.error_empty),
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }),
                modifier = Modifier
                    .weight(0.5f)
                    .padding(start = 4.dp),
                isError = viewModel.nameError.first,
            )
        }
    }
}
