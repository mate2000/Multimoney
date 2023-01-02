package com.multimoney.multimoney.presentation.ui.smart.send.iban.amount

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.ui.smart.send.iban.amount.SmartTransferAmountViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.smart.send.iban.amount.SmartTransferAmountViewModel.UIEvent.OnNavigateHome
import com.multimoney.multimoney.presentation.ui.smart.send.iban.amount.SmartTransferAmountViewModel.UIEvent.OnShareVoucherImage
import com.multimoney.multimoney.presentation.uielement.PaymentSuccessResult
import com.multimoney.multimoney.presentation.uielement.SmartPaymentInfoItem
import com.multimoney.multimoney.presentation.uielement.TopNavBar

@Composable
fun SmartTransferSuccessScreen(
    viewModel: SmartTransferAmountViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.background)
    ) {
        TopNavBar(
            isLeftButtonVisible = false,
            isCenterContentVisible = true,
            onRightButtonClick = {
                viewModel.onUIEvent(OnNavigateHome)
            }
        )
        PaymentSuccessResult(
            onShareClick = { view, bounds ->
                viewModel.onUIEvent(
                    OnShareVoucherImage(view, bounds)
                )
            },
            savePayText = stringResource(
                R.string.smart_payment_you_sent,
                viewModel.uiState.currency
            ),
            amount = viewModel.uiState.currency +
                viewModel.uiState.currentAmountValueString.collectAsState().value,
            fromToText = stringResource(R.string.smart_payment_to_account),
            buttonText = stringResource(R.string.smart_payment_make_another_payment),
            onButtonClick = { viewModel.onUIEvent(OnNavigateBack) }
        ) {
            SmartPaymentInfoItem(
                modifier = Modifier.padding(start = 21.dp, top = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                icon = R.drawable.ic_bank_account,
                iconModifier = Modifier
                    .height(24.dp)
                    .width(24.dp),
                title = stringResource(R.string.smart_payment_origin_account_label),
                subtitle = stringResource(
                    R.string.visa_card_masked_number,
                    viewModel.ibanAccount?.bank ?: ""
                )
            )

            SmartPaymentInfoItem(
                modifier = Modifier.padding(start = 21.dp, top = 32.dp),
                verticalAlignment = Alignment.Top,
                icon = R.drawable.ic_receipt,
                iconModifier = Modifier
                    .height(24.dp)
                    .width(24.dp),
                title = stringResource(R.string.smart_payment_reference_number_label),
                subtitle = viewModel.uiState.referenceNumber
            )

            if (viewModel.shouldDisplayExchange) {
                SmartPaymentInfoItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 21.dp, top = 32.dp, end = 32.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    icon = R.drawable.ic_money_gray,
                    iconModifier = Modifier
                        .height(24.dp)
                        .width(24.dp),
                    title = stringResource(R.string.payment_amount_bottom_sheet_exchange_type),
                    subtitle = viewModel.uiState.exchangeRateLabel,
                    rightTitle = stringResource(R.string.payment_amount_bottom_sheet_amount_to_debit),
                    rightSubtitle = viewModel.uiState.convertedAmountLabel,
                    showVerticalDivision = true
                )
            }

            // Add motive condition
            if (true) {
                SmartPaymentInfoItem(
                    title = stringResource(R.string.smart_payment_motive),
                    icon = R.drawable.ic_notebook_motive,
                    subtitle = "Transferencia cena ayer" // viewModel.uiState.motive
                )
            }

            SmartPaymentInfoItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 21.dp, top = 32.dp, end = 32.dp),
                verticalAlignment = Alignment.CenterVertically,
                icon = R.drawable.ic_calendar,
                iconModifier = Modifier
                    .height(24.dp)
                    .width(24.dp),
                title = viewModel.uiState.currentDate,
                rightSubtitle = viewModel.uiState.currentTime
            )
        }
    }
}
