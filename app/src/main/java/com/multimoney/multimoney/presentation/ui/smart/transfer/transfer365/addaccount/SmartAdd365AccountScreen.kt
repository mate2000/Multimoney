package com.multimoney.multimoney.presentation.ui.smart.transfer.transfer365.addaccount

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.smart.transfer.transfer365.addaccount.SmartAdd365AccountViewModel.UIEvent.OnAccountNumberChanged
import com.multimoney.multimoney.presentation.ui.smart.transfer.transfer365.addaccount.SmartAdd365AccountViewModel.UIEvent.OnAccountTypeSelected
import com.multimoney.multimoney.presentation.ui.smart.transfer.transfer365.addaccount.SmartAdd365AccountViewModel.UIEvent.OnAddFavoriteValueChange
import com.multimoney.multimoney.presentation.ui.smart.transfer.transfer365.addaccount.SmartAdd365AccountViewModel.UIEvent.OnBankSelected
import com.multimoney.multimoney.presentation.ui.smart.transfer.transfer365.addaccount.SmartAdd365AccountViewModel.UIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.smart.transfer.transfer365.addaccount.SmartAdd365AccountViewModel.UIEvent.OnDocumentChanged
import com.multimoney.multimoney.presentation.ui.smart.transfer.transfer365.addaccount.SmartAdd365AccountViewModel.UIEvent.OnDocumentTypeSelected
import com.multimoney.multimoney.presentation.ui.smart.transfer.transfer365.addaccount.SmartAdd365AccountViewModel.UIEvent.OnGetListValues
import com.multimoney.multimoney.presentation.ui.smart.transfer.transfer365.addaccount.SmartAdd365AccountViewModel.UIEvent.OnLastNamesChanged
import com.multimoney.multimoney.presentation.ui.smart.transfer.transfer365.addaccount.SmartAdd365AccountViewModel.UIEvent.OnNamesChanged
import com.multimoney.multimoney.presentation.ui.smart.transfer.transfer365.addaccount.SmartAdd365AccountViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.smart.transfer.transfer365.addaccount.SmartAdd365AccountViewModel.UIEvent.OnNavigateHome
import com.multimoney.multimoney.presentation.ui.smart.transfer.transfer365.addaccount.SmartAdd365AccountViewModel.UIEvent.OnValidateAccountNumber
import com.multimoney.multimoney.presentation.ui.smart.transfer.transfer365.addaccount.SmartAdd365AccountViewModel.UIEvent.OnValidateDocument
import com.multimoney.multimoney.presentation.ui.smart.transfer.transfer365.addaccount.SmartAdd365AccountViewModel.UIEvent.OnValidatePhoneNumber
import com.multimoney.multimoney.presentation.ui.smart.transfer.transfer365.addaccount.SmartAdd365AccountViewModel.UIEvent.OnPhoneChanged
import com.multimoney.multimoney.presentation.ui.smart.transfer.transfer365.addaccount.SmartAdd365AccountViewModel.UIEvent.OnNicknameChanged
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.CustomCheckBox
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.CustomDropdown
import com.multimoney.multimoney.presentation.uielement.CustomDropdownTextField
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.catalog.SmartTransferTypes
import com.multimoney.multimoney.presentation.util.formatDocumentPlaceholder
import com.multimoney.multimoney.presentation.util.transformation.MaskVisualTransformation

@Composable
fun SmartAdd365AccountScreen(
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: SmartAdd365AccountViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {
        viewModel.apply {
            executeNavigation(onNavigate = onNavigate, onPopBackStack = onPopBackStack)
            onUIEvent(OnGetListValues)
        }
    }

    SmartAdd365AccountContent(viewModel)

    if (viewModel.uiState.openDialog.isActive.value) {
        CustomDialog(
            title = stringResource(id = viewModel.uiState.openDialog.titleResource),
            message = stringResource(id = viewModel.uiState.openDialog.descriptionResource).ifEmpty { viewModel.uiState.openDialog.description },
            positiveButtonText = stringResource(id = viewModel.uiState.openDialog.positiveResource),
            negativeButtonText = stringResource(id = viewModel.uiState.openDialog.negativeResource),
            openDialogCustom = viewModel.uiState.openDialog.isActive,
            onDismissAction = viewModel.uiState.openDialog.dismissAction,
            onNegativeAction = viewModel.uiState.openDialog.negativeAction
        )
    }

    LoadingIndicator(viewModel.uiState.isLoading)
}

@Composable
fun SmartAdd365AccountContent(viewModel: SmartAdd365AccountViewModel = hiltViewModel()) {
    val focusManager = LocalFocusManager.current
    Column(
        modifier = Modifier.background(MultimoneyTheme.colors.background)
    ) {
        TopNavBar(
            onLeftButtonClick = { viewModel.onUIEvent(OnNavigateBack) },
            onRightButtonClick = { viewModel.onUIEvent(OnNavigateHome) }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MultimoneyTheme.colors.background)
                .padding(start = 16.dp, end = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(0.9f)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = stringResource(id = viewModel.uiState.screenTitle),
                    modifier = Modifier.padding(top = 32.dp, bottom = 24.dp),
                    style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
                    color = MultimoneyTheme.colors.labelText
                )
                if (viewModel.transferType == SmartTransferTypes.SmartToMobile.id) {
                    Text(
                        text = stringResource(id = viewModel.uiState.screenSubtitle),
                        modifier = Modifier.padding(bottom = 16.dp),
                        style = Typography.subtitle1.copy(fontWeight = FontWeight.SemiBold),
                        color = MultimoneyTheme.colors.subTitleText
                    )
                }
                CustomOutlinedTextField(
                    value = viewModel.uiState.names,
                    labelText = stringResource(id = R.string.names),
                    placeHolder = stringResource(id = R.string.names),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    ),
                    onValueChange = { names ->
                        viewModel.onUIEvent(OnNamesChanged(names))
                    },
                    isRequired = true
                )
                CustomOutlinedTextField(
                    modifier = Modifier.padding(top = 16.dp),
                    value = viewModel.uiState.lastNames,
                    labelText = stringResource(id = R.string.last_names),
                    placeHolder = stringResource(id = R.string.last_names),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    ),
                    onValueChange = { lastNames ->
                        viewModel.onUIEvent(OnLastNamesChanged(lastNames))
                    },
                    isRequired = true
                )
                CustomDropdownTextField(
                    modifier = Modifier.padding(top = 8.dp),
                    labelText = stringResource(id = R.string.document),
                    value = viewModel.uiState.documentNumber,
                    placeHolder = formatDocumentPlaceholder(
                        originFormat = viewModel.uiState.document?.format.orEmpty()
                    ),
                    isError = viewModel.uiState.personalIdError.first,
                    errorMessage = stringResource(
                        id = viewModel.uiState.personalIdError.second,
                        viewModel.uiState.document?.description.orEmpty()
                    ),
                    onValueChange = { documentNumber ->
                        viewModel.onUIEvent(OnDocumentChanged(documentNumber))
                    },
                    onSelectionChange = { documentType, _ ->
                        viewModel.onUIEvent(OnDocumentTypeSelected(documentType))
                    },
                    optionList = viewModel.uiState.documentList.map { it.description },
                    optionSelected = viewModel.uiState.document?.description.orEmpty(),
                    customTransformation = if (viewModel.uiState.document?.format?.isNotBlank() == true) MaskVisualTransformation(
                        viewModel.uiState.document?.format.orEmpty(),
                        viewModel.uiState.document?.format.orEmpty().last()
                    ) else null,
                    onDebounceValidation = {
                        viewModel.onUIEvent(OnValidateDocument)
                    }
                )
                CustomDropdown(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .wrapContentSize(Alignment.TopStart)
                        .focusable(false),
                    items = viewModel.uiState.bankList.map { it.bankName.orEmpty() },
                    value = viewModel.uiState.bank?.bankName.orEmpty(),
                    onValueChange = { valueSelected, _ ->
                        viewModel.onUIEvent(OnBankSelected(valueSelected))
                    },
                    labelText = stringResource(id = R.string.destination_bank),
                    placeHolder = stringResource(id = R.string.select)
                )
                CustomDropdown(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .wrapContentSize(Alignment.TopStart)
                        .focusable(false),
                    items = viewModel.uiState.accountTypeList.map { it?.typeName.orEmpty() },
                    value = viewModel.uiState.type?.typeName.orEmpty(),
                    onValueChange = { valueSelected, _ ->
                        viewModel.onUIEvent(OnAccountTypeSelected(valueSelected))
                    },
                    labelText = stringResource(id = viewModel.uiState.typeAccountLabel),
                    placeHolder = stringResource(id = R.string.select)
                )
                if (viewModel.transferType == SmartTransferTypes.SmartToOtherBank.id) {
                    CustomOutlinedTextField(
                        modifier = Modifier.padding(top = 16.dp),
                        value = viewModel.uiState.accountNumber,
                        onValueChange = { viewModel.onUIEvent(OnAccountNumberChanged(it)) },
                        leadingIcon = R.drawable.ic_account_new,
                        labelText = stringResource(id = R.string.account_number),
                        placeHolder = stringResource(id = R.string.nine_digits_placeholder),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                            }
                        ),
                        isError = viewModel.uiState.isAccountNumberError,
                        errorMessage = stringResource(id = R.string.smart_other_bank_transfer_add_account_number_error),
                        onDebounceValidation = { viewModel.onUIEvent(OnValidateAccountNumber) }
                    )
                    CustomCheckBox(
                        modifier = Modifier.padding(top = 16.dp),
                        checked = viewModel.uiState.isFavorite,
                        onCheckedChange = {
                            viewModel.onUIEvent(OnAddFavoriteValueChange(it))
                        },
                        text = stringResource(id = R.string.smart_iban_register_favorite_checkbox)
                    )
                    if (viewModel.uiState.isFavorite) {
                        CustomOutlinedTextField(
                            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
                            value = viewModel.uiState.nickname,
                            labelText = stringResource(id = R.string.smart_iban_register_favorite_label),
                            placeHolder = stringResource(id = R.string.smart_iban_register_favorite_label),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                }
                            ),
                            onValueChange = { nickname ->
                                viewModel.onUIEvent(OnNicknameChanged(nickname))
                            },
                            isRequired = false
                        )
                    }
                } else if (viewModel.transferType == SmartTransferTypes.SmartToMobile.id) {
                    CustomOutlinedTextField(
                        modifier = Modifier.padding(top = 8.dp),
                        value = viewModel.uiState.phoneNumber,
                        onValueChange = { viewModel.onUIEvent(OnPhoneChanged(it)) },
                        labelText = stringResource(id = R.string.phone_number),
                        placeHolder = stringResource(id = R.string.credit_job_phone_placeholder),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                            }
                        ),
                        isError = viewModel.uiState.isPhoneNumberError,
                        errorMessage = stringResource(id = R.string.smart_other_bank_transfer_add_phone_number_error),
                        onDebounceValidation = { viewModel.onUIEvent(OnValidatePhoneNumber) }
                    )
                }
            }
            CustomButton(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .height(48.dp)
                    .fillMaxWidth(),
                onClick = { viewModel.onUIEvent(OnContinueClick) },
                buttonType = CustomButtonType.PrimaryPrimary,
                text = stringResource(id = R.string.button_continue),
                enable = viewModel.uiState.enableButton
            )
        }
    }
}