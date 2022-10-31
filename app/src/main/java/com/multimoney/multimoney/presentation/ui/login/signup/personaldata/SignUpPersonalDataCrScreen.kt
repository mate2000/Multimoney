package com.multimoney.multimoney.presentation.ui.login.signup.personaldata

import androidx.compose.foundation.focusable
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
import androidx.compose.ui.focus.FocusDirection
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
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent.OnSharedIdentificationValueChange
import com.multimoney.multimoney.presentation.ui.login.signup.personaldata.SignUpPersonalDataViewModel.Companion.FORMAT_VALUE
import com.multimoney.multimoney.presentation.ui.login.signup.personaldata.SignUpPersonalDataViewModel.UIEvent.OnIdentificationTypeValueChange
import com.multimoney.multimoney.presentation.ui.login.signup.personaldata.SignUpPersonalDataViewModel.UIEvent.OnIdentificationValueChange
import com.multimoney.multimoney.presentation.uielement.CustomDropdown
import com.multimoney.multimoney.presentation.uielement.CustomImage
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.util.CrDocuments
import com.multimoney.multimoney.presentation.util.capitalized
import com.multimoney.multimoney.presentation.util.transformation.MaskVisualTransformation

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
        CustomDropdown(
            modifier = Modifier
                .wrapContentSize(Alignment.TopStart)
                .focusable(false)
                .padding(top = 16.dp),
            items = viewModel.uiState.documentList,
            onValueChange = {
                viewModel.onUIEvent(OnIdentificationTypeValueChange(it))
            },
            labelText = stringResource(id = R.string.sign_up_personal_data_document_label),
            value = viewModel.uiState.identificationValueType,
            placeHolder = stringResource(id = R.string.sign_up_personal_data_document_hint)
        )
        CustomOutlinedTextField(
            value = viewModel.uiState.personalDocumentValue,
            placeHolder = if (viewModel.uiState.documentFormat != "") viewModel.uiState.documentFormat.replace(
                viewModel.uiState.documentFormat.last(),
                FORMAT_VALUE,
                false
            ) else "",
            onValueChange = { document ->
                viewModel.onUIEvent(
                    OnIdentificationValueChange(document) {
                        sharedViewModel.onUIEvent(
                            OnSharedIdentificationValueChange(document)
                        )
                    }
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            }),
            labelText = stringResource(id = R.string.sign_up_personal_data_document_number_label),
            modifier = Modifier.padding(top = 44.dp),
            isRequired = true,
            isRequiredMessage = stringResource(id = R.string.sign_up_personal_data_id_required),
            isError = viewModel.uiState.personalIdError.first,
            errorMessage = stringResource(id = viewModel.uiState.personalIdError.second),
            customTransformation = if (viewModel.uiState.documentFormat != "") MaskVisualTransformation(
                viewModel.uiState.documentFormat,
                viewModel.uiState.documentFormat.last()
            ) else null,
            onDebounceValidation = {
                viewModel.onUIEvent(
                    SignUpPersonalDataViewModel.UIEvent.OnValidateDocument(
                        document = sharedViewModel.userData?.email
                    )
                )
            }
        )

        if (viewModel.uiState.identificationValueType != CrDocuments.IdDocument.document) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 44.dp)
            ) {
                CustomOutlinedTextField(
                    placeHolder = stringResource(id = R.string.sign_up_personal_data_first_name_hint),
                    value = viewModel.uiState.firstNameValue,
                    onValueChange = { firstName ->
                        viewModel.onUIEvent(
                            SignUpPersonalDataViewModel.UIEvent.OnFirstNameChange(
                                firstName = firstName,
                                onSharedViewModelFirstNameChange = {
                                    SignUpViewModel.UIEvent.OnFirstNameValueChange(
                                        firstName
                                    )
                                }
                            )
                        )
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
                    isError = viewModel.uiState.nameError.first
                )

                CustomOutlinedTextField(
                    placeHolder = stringResource(id = R.string.sign_up_personal_data_second_name_hint),
                    value = viewModel.uiState.secondNameValue,
                    onValueChange = { secondName ->
                        viewModel.onUIEvent(
                            SignUpPersonalDataViewModel.UIEvent.OnSecondNameChange(
                                secondName = secondName,
                                onSharedViewModelSecondNameChange = {
                                    SignUpViewModel.UIEvent.OnSecondNameValueChange(
                                        secondName
                                    )
                                }
                            )
                        )
                    },
                    isRequired = false,
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
                    onValueChange = { firstLastName ->
                        viewModel.onUIEvent(
                            SignUpPersonalDataViewModel.UIEvent.OnFirstLastNameChange(
                                firstLastName = firstLastName,
                                onSharedViewModelFirstLastNameChange = {
                                    SignUpViewModel.UIEvent.OnFirstLastNameValueChange(
                                        firstLastName
                                    )
                                }
                            )
                        )
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
                    isError = viewModel.uiState.lastNameError.first
                )

                CustomOutlinedTextField(
                    placeHolder = stringResource(id = R.string.sign_up_personal_data_second_lastname_hint),
                    value = viewModel.uiState.secondLastNameValue,
                    isRequired = false,
                    onValueChange = { secondLastName ->
                        viewModel.onUIEvent(
                            SignUpPersonalDataViewModel.UIEvent.OnSecondLastNameChange(
                                secondLastName = secondLastName,
                                onSharedViewModelSecondLastNameChange = {
                                    SignUpViewModel.UIEvent.OnSecondLastNameValueChange(
                                        secondLastName
                                    )
                                }
                            )
                        )
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
        } else {
            if (viewModel.uiState.isLoading) {
                sharedViewModel.userData?.fullName = null
                Row(modifier = Modifier.padding(top = 12.dp)) {
                    CustomImage(
                        drawableResource = R.drawable.ic_time,
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

            if (viewModel.uiState.dataInformationClient?.name.isNullOrBlank().not()) {
                viewModel.uiState.dataInformationClient?.apply {
                    viewModel.onUIEvent(SignUpPersonalDataViewModel.UIEvent.OnValidateForm)
                    viewModel.onUIEvent(
                        SignUpPersonalDataViewModel.UIEvent.OnUpdateAllNames(
                            firstName = firstName.capitalized(),
                            secondName = secondName.capitalized(),
                            firstLastName = firstLastName.capitalized(),
                            secondLastName = secondLastName.capitalized(),
                            onUpdateAllNamesInShareViewModel = {
                                sharedViewModel.onUIEvent(
                                    SignUpViewModel.UIEvent.OnUpdateUserNames(
                                        fullName = name,
                                        firstName = firstName.capitalized(),
                                        secondName = secondName.capitalized(),
                                        firstLastName = firstLastName.capitalized(),
                                        secondLastName = secondLastName.capitalized()
                                    )
                                )
                            }
                        )
                    )
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
                            color = MultimoneyTheme.colors.textSuccess
                        )
                    )
                }
                Text(
                    modifier = Modifier.padding(top = 8.dp, start = 4.dp),
                    text = viewModel.uiState.dataInformationClient?.name.toString(),
                    style = Typography.body2.copy(color = MultimoneyTheme.colors.text)
                )
            }
        }
    }

    if (viewModel.closeKeyboard) {
        focusManager.clearFocus()
        viewModel.closeKeyboard = false
    }
}
