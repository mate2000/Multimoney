package com.multimoney.multimoney.presentation.ui.smart.transfer.addaccount

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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.domain.model.accountsmart.SmartAccountType
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.smart.transfer.addaccount.SmartAddAccountViewModel.UIEvent.OnAccountNumberChanged
import com.multimoney.multimoney.presentation.ui.smart.transfer.addaccount.SmartAddAccountViewModel.UIEvent.OnAccountTypeSelected
import com.multimoney.multimoney.presentation.ui.smart.transfer.addaccount.SmartAddAccountViewModel.UIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.smart.transfer.addaccount.SmartAddAccountViewModel.UIEvent.OnEmailChanged
import com.multimoney.multimoney.presentation.ui.smart.transfer.addaccount.SmartAddAccountViewModel.UIEvent.OnGetAccountTypes
import com.multimoney.multimoney.presentation.ui.smart.transfer.addaccount.SmartAddAccountViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.smart.transfer.addaccount.SmartAddAccountViewModel.UIEvent.OnValidateAccountNumber
import com.multimoney.multimoney.presentation.ui.smart.transfer.addaccount.SmartAddAccountViewModel.UIEvent.OnValidateUserEmail
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.CustomDropdown
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun SmartAddAccountScreen(
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: SmartAddAccountViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {
        viewModel.apply {
            executeNavigation(onNavigate = onNavigate, onPopBackStack = onPopBackStack)
            onUIEvent(OnGetAccountTypes)
        }
    }

    SmartAddAccountContent(viewModel)

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
fun SmartAddAccountContent(viewModel: SmartAddAccountViewModel = hiltViewModel()) {
    val focusManager = LocalFocusManager.current
    Column(
        modifier = Modifier.background(MultimoneyTheme.colors.background)
    ) {
        TopNavBar(
            onLeftButtonClick = { viewModel.onUIEvent(OnNavigateBack) },
            isRightButtonVisible = false
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(MultimoneyTheme.colors.background)
                .padding(start = 16.dp, end = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = stringResource(id = R.string.smart_add_sac_account_title),
                    modifier = Modifier.padding(top = 32.dp),
                    style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
                    color = MultimoneyTheme.colors.labelText
                )
                CustomDropdown(
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .wrapContentSize(Alignment.TopStart)
                        .focusable(false),
                    items = viewModel.uiState.accountTypeList.map { it?.typeName.orEmpty() },
                    value = viewModel.uiState.type?.typeName ?: "",
                    onValueChange = { valueSelected, _ ->
                        viewModel.onUIEvent(OnAccountTypeSelected(valueSelected))
                    },
                    labelText = stringResource(id = R.string.smart_add_sac_account_type_label),
                    placeHolder = stringResource(id = R.string.select)
                )
                CustomOutlinedTextField(
                    modifier = Modifier.padding(top = 16.dp),
                    value = viewModel.uiState.accountNumber,
                    onValueChange = { viewModel.onUIEvent(OnAccountNumberChanged(it)) },
                    leadingIcon = R.drawable.ic_account_new,
                    labelText = stringResource(id = R.string.smart_add_sac_account_number_label),
                    placeHolder = stringResource(id = R.string.smart_add_sac_account_number_placeholder),
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
                    errorMessage = stringResource(id = R.string.smart_add_sac_account_number_validation),
                    onDebounceValidation = { viewModel.onUIEvent(OnValidateAccountNumber) }
                )
                CustomOutlinedTextField(
                    modifier = Modifier.padding(vertical = 16.dp),
                    value = viewModel.uiState.email,
                    labelText = stringResource(id = R.string.smart_add_sac_account_email_label),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                        }
                    ),
                    onValueChange = { email ->
                        viewModel.onUIEvent(OnEmailChanged(email))
                    },
                    isError = viewModel.uiState.emailError.first,
                    errorMessage = stringResource(id = viewModel.uiState.emailError.second),
                    isRequired = true,
                    onDebounceValidation = { viewModel.onUIEvent(OnValidateUserEmail) },
                )
            }
            CustomButton(
                modifier = Modifier
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