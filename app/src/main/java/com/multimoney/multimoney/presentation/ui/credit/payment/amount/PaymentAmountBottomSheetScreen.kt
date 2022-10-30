package com.multimoney.multimoney.presentation.ui.credit.payment.amount

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.credit.origination.creditamount.CreditAmountViewModel.Companion.CURRENCY_SEPARATOR
import com.multimoney.multimoney.presentation.ui.credit.payment.amount.PaymentAmountViewModel.UIEvent
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.CustomCheckBox
import com.multimoney.multimoney.presentation.uielement.CustomInfoButton
import com.multimoney.multimoney.presentation.uielement.CustomModalBottomSheet
import com.multimoney.multimoney.presentation.util.getCurrency
import com.multimoney.multimoney.presentation.util.getMaskedAccount
import com.multimoney.multimoney.presentation.util.stringToIntegerFormat
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PaymentAmountBottomSheetScreen(
    viewModel: PaymentAmountViewModel,
    coroutineScope: CoroutineScope,
    modalBottomSheetState: ModalBottomSheetState,
    paymentDescription: String
) {
    CustomModalBottomSheet(
        title = R.string.payment_amount_bottom_sheet_title,
        closeIcon = R.drawable.ic_close_bottom_sheet,
        modalBottomSheetState = modalBottomSheetState,
        coroutineScope = coroutineScope
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            if (viewModel.isMultiCurrency()) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = getMultiCurrencyAmountIncludingExchange(viewModel),
                    style = Typography.h4.copy(fontWeight = FontWeight.W600),
                    color = MultimoneyTheme.colors.text,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = viewModel.uiState.currentAmountValueString,
                    style = Typography.body2.copy(fontWeight = FontWeight.W600),
                    color = MultimoneyTheme.colors.text,
                    textAlign = TextAlign.Center
                )
            } else if (viewModel.shouldDisplayExchangeRate()) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "${viewModel.uiState.currency}${viewModel.uiState.exchangeConvertedAmount.toString().stringToIntegerFormat(CURRENCY_SEPARATOR.toString())}",
                    style = Typography.h4.copy(fontWeight = FontWeight.W600),
                    color = MultimoneyTheme.colors.text,
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "${viewModel.uiState.currency}${viewModel.uiState.currentAmountValueString.stringToIntegerFormat(CURRENCY_SEPARATOR.toString())}",
                    style = Typography.h4.copy(fontWeight = FontWeight.W600),
                    color = MultimoneyTheme.colors.text,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = stringResource(id = R.string.payment_amount_bottom_sheet_account),
                style = Typography.body2.copy(fontWeight = FontWeight.W600),
                color = MultimoneyTheme.colors.text,
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.height(4.dp))
            CustomInfoButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                startIcon = viewModel.uiState.clientBankAccount?.idCurrency?.getCurrency()?.accountIcon ?: 0,
                title = viewModel.uiState.clientBankAccount?.bankDescription ?: "",
                subtitle = getMaskedAccount(
                    viewModel.uiState.clientBankAccount?.accountNumber ?: "",
                    stringResource(id = R.string.payment_account_masked_text)
                )
            )
            if (viewModel.shouldDisplayExchangeRate()) {
                Spacer(modifier = Modifier.height(32.dp))
                CurrencyExchangeRow(viewModel)
            }
            if (viewModel.isMultiCurrency()) {
                Spacer(modifier = Modifier.height(32.dp))
                CustomCheckBox(
                    checked = viewModel.uiState.isAutomaticProgrammedPaymentChecked,
                    onCheckedChange = { viewModel.onUIEvent(UIEvent.OnAutomaticProgrammedPaymentCheckedChanged(it)) },
                    text = stringResource(id = R.string.payment_amount_bottom_sheet_enable_automatic_payment)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
            CustomButton(
                modifier = Modifier
                    .height(48.dp)
                    .fillMaxWidth(),
                onClick = {
                    viewModel.onUIEvent(UIEvent.OnProcessPayment(paymentDescription = paymentDescription))
                },
                text = stringResource(id = R.string.payment_amount_bottom_sheet_button),
                buttonType = CustomButtonType.PrimaryPrimary,
                enable = viewModel.uiState.isLoading.not()
            )
        }
    }
}

@Composable
fun CurrencyExchangeRow(viewModel: PaymentAmountViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Text(
                text = stringResource(id = R.string.payment_amount_bottom_sheet_exchange_type),
                style = Typography.body2.copy(fontWeight = FontWeight.W600),
                color = MultimoneyTheme.colors.text,
                textAlign = TextAlign.Start
            )
            Text(
                text = viewModel.uiState.exchangeRateLabel.toString(),
                style = Typography.body2,
                color = MultimoneyTheme.colors.text,
                textAlign = TextAlign.Start
            )
        }
        Spacer(modifier = Modifier.width(40.dp))
        Divider(
            modifier = Modifier
                .height(44.dp)
                .width(1.dp),
            color = MultimoneyTheme.colors.bottomNavigationDividerColor
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = stringResource(id = R.string.payment_amount_bottom_sheet_amount_to_debit),
                style = Typography.body2.copy(fontWeight = FontWeight.W600),
                color = MultimoneyTheme.colors.text,
                textAlign = TextAlign.Start
            )
            Text(
                text = viewModel.uiState.exchangeConvertedAmount.toString(),
                style = Typography.body2,
                color = MultimoneyTheme.colors.text,
                textAlign = TextAlign.Start
            )
        }
    }
}

fun getMultiCurrencyAmountIncludingExchange(viewModel: PaymentAmountViewModel): String {
    val balance = viewModel.summaryList?.find { it.idCurrency == viewModel.uiState.clientBankAccount?.idCurrency }?.currentBalance ?: 0.0
    val exchangedAmount = viewModel.uiState.exchangeConvertedAmount
    val total = balance + exchangedAmount
    return viewModel.uiState.currency + total
}
