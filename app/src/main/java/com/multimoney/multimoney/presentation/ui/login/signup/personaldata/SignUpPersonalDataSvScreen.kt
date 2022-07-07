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
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnSharedIdentificationValueChange
import com.multimoney.multimoney.presentation.ui.login.signup.personaldata.SignUpPersonalDataViewModel.UIEvent.OnFirstLastNameChange
import com.multimoney.multimoney.presentation.ui.login.signup.personaldata.SignUpPersonalDataViewModel.UIEvent.OnFirstNameChange
import com.multimoney.multimoney.presentation.ui.login.signup.personaldata.SignUpPersonalDataViewModel.UIEvent.OnIdentificationTypeValueChange
import com.multimoney.multimoney.presentation.ui.login.signup.personaldata.SignUpPersonalDataViewModel.UIEvent.OnIdentificationValueChange
import com.multimoney.multimoney.presentation.ui.login.signup.personaldata.SignUpPersonalDataViewModel.UIEvent.OnSecondLastNameChange
import com.multimoney.multimoney.presentation.ui.login.signup.personaldata.SignUpPersonalDataViewModel.UIEvent.OnSecondNameChange
import com.multimoney.multimoney.presentation.ui.login.signup.personaldata.SignUpPersonalDataViewModel.UIEvent.OnValidateDocument
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
                viewModel.onUIEvent(OnIdentificationTypeValueChange(it))
            },
            labelText = stringResource(id = R.string.sign_up_personal_data_document_label),
            value = viewModel.uiState.identificationValueType,
            placeHolder = stringResource(id = R.string.sign_up_personal_data_document_hint)
        )
        CustomOutlinedTextField(
            value = viewModel.uiState.personalDocumentValue,
            placeHolder = stringResource(id = R.string.sign_up_personal_data_sv_id_hint),
            onValueChange = { text ->
                viewModel.onUIEvent(OnIdentificationValueChange(text, {
                    sharedViewModel.onUIEvent(
                        OnSharedIdentificationValueChange(text)
                    )
                }, Nationalities.ElSalvador.documentSize))
            },
            onDebounceValidation = {
                viewModel.onUIEvent(
                    OnValidateDocument(
                        { validDui(viewModel.uiState.personalDocumentValue) },
                        { sharedViewModel.onUIEvent(OnContinueEnable(viewModel.validateFields())) })
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            }),
            labelText = stringResource(id = R.string.sign_up_personal_data_document_number_label),
            modifier = Modifier
                .padding(top = 44.dp),
            isRequired = true,
            isRequiredMessage = stringResource(id = R.string.sign_up_personal_data_id_sv_required),
            isError = viewModel.uiState.personalIdError.first,
            errorMessage = stringResource(id = viewModel.uiState.personalIdError.second),
            customTransformation = formatDui()
        )

        if (viewModel.uiState.identificationValueType.isNotBlank()) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 44.dp)
            ) {
                CustomOutlinedTextField(
                    placeHolder = stringResource(id = R.string.sign_up_personal_data_first_name_hint),
                    value = viewModel.uiState.firstNameValue,
                    onValueChange = {
                        viewModel.onUIEvent(OnFirstNameChange(it))
                        // TODO pasar en onfirst name change, reciba userdatavaluechange(un evento)
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
                    isError = viewModel.uiState.nameError.first,
                )

                CustomOutlinedTextField(
                    placeHolder = stringResource(id = R.string.sign_up_personal_data_second_name_hint),
                    value = viewModel.uiState.secondNameValue,
                    onValueChange = {
                        viewModel.onUIEvent(OnSecondNameChange(it))
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
                        .padding(start = 6.dp)
                )
            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 44.dp)
            ) {
                CustomOutlinedTextField(
                    placeHolder = stringResource(id = R.string.sign_up_personal_data_first_lastname_hint),
                    value = viewModel.uiState.firstLastNameValue,
                    onValueChange = {
                        viewModel.onUIEvent(OnFirstLastNameChange(it))
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
                        .weight(0.5f)
                        .padding(top = 4.dp),
                    isRequired = true,
                    isRequiredMessage = stringResource(id = R.string.sign_up_personal_data_lastname_error),
                    isError = viewModel.uiState.lastNameError.first,
                )

                CustomOutlinedTextField(
                    placeHolder = stringResource(id = R.string.sign_up_personal_data_second_lastname_hint),
                    value = viewModel.uiState.secondLastNameValue,
                    onValueChange = {
                        viewModel.onUIEvent(OnSecondLastNameChange(it))
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
                        .padding(start = 6.dp)
                )
            }
        }
    }
}
