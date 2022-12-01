package com.multimoney.multimoney.presentation.ui.credit.disbursement.addaccount

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.domain.model.credit.CreditCatalogOption
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.credit.disbursement.addaccount.DisbursementAddAccountViewModel.UIEvent.OnCallQueryBanksAndRegularExpression
import com.multimoney.multimoney.presentation.ui.credit.disbursement.addaccount.DisbursementAddAccountViewModel.UIEvent.OnBackClick
import com.multimoney.multimoney.presentation.ui.credit.disbursement.addaccount.DisbursementAddAccountViewModel.UIEvent.OnBankValueChanged
import com.multimoney.multimoney.presentation.ui.credit.disbursement.addaccount.DisbursementAddAccountViewModel.UIEvent.OnAccountTypeValueChanged
import com.multimoney.multimoney.presentation.ui.credit.disbursement.addaccount.DisbursementAddAccountViewModel.UIEvent.OnAccountNumberValueChange
import com.multimoney.multimoney.presentation.ui.credit.disbursement.addaccount.DisbursementAddAccountViewModel.UIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.uielement.CustomDropdown
import com.multimoney.multimoney.presentation.uielement.CustomInformativeText
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters

@Composable
fun DisbursementAddAccountScreen(
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: DisbursementAddAccountViewModel = hiltViewModel()
) {
    // Properties

    val focusManager = LocalFocusManager.current

    // Navigation

    LaunchedEffect(true) {
        viewModel.executeNavigation(onNavigate = onNavigate, onPopBackStack = onPopBackStack)
        viewModel.onUIEvent(OnCallQueryBanksAndRegularExpression)
    }

    // View

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.background)

    ) {
        Column {
            TopBar(onBackClick = {
                viewModel.onUIEvent(OnBackClick(focusManager))
            })
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Title(title = viewModel.uiState.titleResource)
                Subtitle()
                BankDestiny(
                    items = viewModel.uiState.bankList,
                    value = viewModel.uiState.bankSelected,
                    onValueChange = {
                        viewModel.onUIEvent(
                            OnBankValueChanged(it)
                        )
                    }
                )
                AccountType(
                    items = viewModel.uiState.accountTypeListFiltered?.map { it?.description ?: "" }
                        ?: listOf(),
                    value = viewModel.uiState.accountTypeSelectedString,
                    onValueChange = { value ->
                        viewModel.onUIEvent(
                            OnAccountTypeValueChanged(
                                viewModel.uiState.accountTypeListFiltered?.findLast { it?.description == value })
                        )
                    })
                AccountNumber(
                    value = viewModel.uiState.accountNumber,
                    onValueChange = {
                        viewModel.onUIEvent(
                            OnAccountNumberValueChange(it)
                        )
                    },
                    onError = viewModel.uiState.accountNumberError,
                    focusManager = focusManager
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Continue(
                enable = viewModel.uiState.isContinueEnabled,
                onClick = { viewModel.onUIEvent(OnContinueClick(focusManager)) }
            )
        }
    }
    LoadingIndicator(viewModel.uiState.isLoading)
    ErrorDialog(viewModel.uiState.openDialog)
}

@Composable
private fun ErrorDialog(openDialog: DialogParameters) {
    if (openDialog.isActive.value) {
        CustomDialog(
            title = stringResource(id = openDialog.titleResource),
            message = openDialog.description,
            positiveButtonText = stringResource(id = openDialog.positiveResource),
            negativeButtonText = stringResource(id = openDialog.negativeResource),
            openDialogCustom = openDialog.isActive,
            onPositiveAction = openDialog.positiveAction
        )
    }
}

@Composable
private fun Continue(enable: Boolean, onClick: () -> Unit) {
    CustomButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(48.dp),
        onClick = onClick,
        buttonType = CustomButtonType.PrimaryPrimary,
        text = stringResource(id = R.string.button_continue),
        enable = enable
    )
}

@Composable
private fun AccountNumber(
    value: String,
    onValueChange: (String) -> Unit,
    onError: Pair<Boolean, Int>,
    focusManager: FocusManager
) {
    CustomOutlinedTextField(
        modifier = Modifier
            .padding(top = 16.dp),
        value = value,
        onValueChange = onValueChange,
        leadingIcon = R.drawable.ic_account,
        labelText = stringResource(id = R.string.credit_bank_account_number_label),
        placeHolder = stringResource(id = R.string.credit_bank_account_number_hint),
        isRequiredMessage = stringResource(id = R.string.credit_bank_account_number_required),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
            }
        ),
        isError = onError.first,
        errorMessage = stringResource(id = onError.second)
    )
}

@Composable
private fun AccountType(
    items: List<String>,
    value: String,
    onValueChange: (String) -> Unit
) {
    CustomDropdown(
        modifier = Modifier
            .padding(top = 16.dp)
            .wrapContentSize(Alignment.TopStart)
            .focusable(false),
        items = items,
        value = value,
        onValueChange = onValueChange,
        labelText = stringResource(id = R.string.credit_bank_account_type),
        placeHolder = stringResource(id = R.string.select)
    )
}

@Composable
private fun BankDestiny(
    items: List<CreditCatalogOption?>?,
    value: CreditCatalogOption?,
    onValueChange: (CreditCatalogOption?) -> Unit
) {
    CustomDropdown(
        modifier = Modifier
            .padding(top = 16.dp)
            .wrapContentSize(Alignment.TopStart)
            .focusable(false),
        items = items,
        value = value,
        onValueChange = onValueChange,
        labelText = stringResource(id = R.string.credit_bank_account_destiny),
        placeHolder = stringResource(id = R.string.select)
    )
}

@Composable
private fun Subtitle() {

    CustomInformativeText(
        modifier = Modifier.padding(top = 8.dp),
        leadingIcon = R.drawable.ic_information,
        text = stringResource(id = R.string.credit_bank_condition),
        textStyle = Typography.body2.copy(color = MultimoneyTheme.colors.labelText)
    )
}

@Composable
private fun Title(title: Int) {
    Text(
        text = buildAnnotatedString {
            withStyle(
                style = Typography.h6.toSpanStyle()
                    .copy(
                        color = MultimoneyTheme.colors.text,
                        fontWeight = FontWeight.SemiBold
                    )
            ) {
                append(stringResource(id = title))
            }
        },
        textAlign = TextAlign.Start,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun TopBar(
    onBackClick: () -> Unit
) {
    TopNavBar(
        isLeftButtonVisible = true,
        isRightButtonVisible = false,
        onLeftButtonClick = onBackClick
    )
}
