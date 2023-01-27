package com.multimoney.multimoney.presentation.ui.credit.origination.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.credit.disbursement.account.DisbursementAccountViewModel
import com.multimoney.multimoney.presentation.ui.credit.disbursement.account.DisbursementBottomSheetScreen
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel
import com.multimoney.multimoney.presentation.uielement.*
import com.multimoney.multimoney.presentation.util.getCurrencyFromId
import com.multimoney.multimoney.presentation.util.getMaskedAccount
import kotlinx.coroutines.CoroutineScope

@Composable
fun CrosselingAccountScreen(
    sharedViewModel: CreditViewModel,
    viewModel: CrosselingAccountViewModel = hiltViewModel()
) {



}

@OptIn(ExperimentalMaterialApi::class)
@Composable
@Preview
fun PaymentAccountContent(
    viewModel: DisbursementAccountViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier
            .background(MultimoneyTheme.colors.background)
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                modifier = Modifier.padding(top = 42.dp, start = 16.dp, end = 16.dp, bottom = 20.dp),
                text = stringResource(id = viewModel.uiState.titleResource),
                style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
                color = MultimoneyTheme.colors.labelText,
                textAlign = TextAlign.Left
            )

            PaymentAccountList(viewModel)

            if (viewModel.uiState.openDialog.isActive.value) {
                CustomDialog(
                    title = stringResource(id = viewModel.uiState.openDialog.titleResource),
                    message = stringResource(id = viewModel.uiState.openDialog.descriptionResource).ifEmpty { viewModel.uiState.openDialog.description },
                    positiveButtonText = stringResource(id = viewModel.uiState.openDialog.positiveResource),
                    openDialogCustom = viewModel.uiState.openDialog.isActive,
                    onPositiveAction = viewModel.uiState.openDialog.positiveAction
                )
            }
        }

        if ((viewModel.uiState.clientBankAccountList?.size ?: 0) >= DisbursementAccountViewModel.MAX_ACCOUNT_NUMBER) {
            CustomInformativeText(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 32.dp, top = 16.dp),
                leadingIcon = R.drawable.ic_informative_400,
                text = stringResource(id = R.string.disbursement_account_max_number_disclaimer),
                textStyle = Typography.body2.copy(color = MultimoneyTheme.colors.labelText),
                leadingIconClick = { viewModel.onUIEvent(DisbursementAccountViewModel.UIEvent.OnDisclaimerClick) }
            )
        }
    }
    LoadingIndicator(viewModel.uiState.isLoading)
}

@Composable
@Preview
fun PaymentAccountList(
    viewModel: DisbursementAccountViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    viewModel.uiState.clientBankAccountList?.let { clientBankAccountList ->
        LazyColumn(modifier = Modifier.padding(top = 20.dp, start = 16.dp, end = 16.dp)) {
            items(clientBankAccountList) { clientBankAccount ->
                CustomInfoButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    startIcon = clientBankAccount?.idCurrency?.getCurrencyFromId()?.accountIcon ?: 0,
                    title = clientBankAccount?.bankDescription ?: "",
                    subtitle = getMaskedAccount(
                        clientBankAccount?.accountNumber ?: "",
                        stringResource(id = R.string.payment_account_masked_text)
                    ),
                    endIcon = com.novopayment.sdk.vts.R.drawable.ic_arrow_right_novo_sdk,
                    onClick = {
                        viewModel.onUIEvent(DisbursementAccountViewModel.UIEvent.OnClientBankAccountSelected(clientBankAccount))
                    }
                )
            }
        }
    }
    CustomButton(
        text = stringResource(id = R.string.payment_account_create),
        modifier = Modifier
            .padding(top = 32.dp)
            .fillMaxWidth(),
        onClick = {
            viewModel.onUIEvent(DisbursementAccountViewModel.UIEvent.OnNavigateToDisbursementAddAccount)
        },
        buttonType = CustomButtonType.PrimaryTertiary,
        trailingIcon = R.drawable.ic_plus,
        enable = (viewModel.uiState.clientBankAccountList?.size ?: 0) < DisbursementAccountViewModel.MAX_ACCOUNT_NUMBER
    )
}