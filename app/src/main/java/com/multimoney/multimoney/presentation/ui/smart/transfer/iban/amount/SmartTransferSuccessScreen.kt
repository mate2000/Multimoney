package com.multimoney.multimoney.presentation.ui.smart.transfer.iban.amount

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel.AmountUIEvent.OnNavigateHome
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel.AmountUIEvent.OnShareVoucherImage
import com.multimoney.multimoney.presentation.uielement.PaymentSuccessResult
import com.multimoney.multimoney.presentation.uielement.SmartPaymentInfoItem
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.getMaskedAccountIban

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
                viewModel.onAmountUIEvent(OnNavigateHome)
            }
        )
        PaymentSuccessResult(
            onShareClick = { view, bounds ->
                viewModel.onAmountUIEvent(
                    OnShareVoucherImage(view, bounds)
                )
            },
            savePayText = stringResource(
                R.string.smart_payment_you_sent,
                viewModel.amountUIState.currency
            ),
            amount = viewModel.getFormattedAmount(),
            exchangedAmount = if (viewModel.shouldDisplayExchange) {
                viewModel.amountUIState.convertedAmountLabel
            } else {
                null
            },
            fromToText = stringResource(R.string.smart_payment_amount_bottom_sheet_to),
            showButton = false
        ) {
            SmartPaymentInfoItem(
                verticalAlignment = Alignment.CenterVertically,
                icon = viewModel.amountUIState.destinyAccountDisplay?.icon,
                title = stringResource(R.string.smart_payment_destiny_account_label),
                subtitle = getMaskedAccountIban(
                    viewModel.ibanAccount?.sinpeAccount ?: ""
                )
            )

            SmartPaymentInfoItem(
                modifier = Modifier.padding(top = 24.dp),
                verticalAlignment = Alignment.Top,
                icon = R.drawable.ic_receipt,
                title = stringResource(R.string.smart_payment_reference_number_label),
                subtitle = viewModel.amountUIState.referenceNumber
            )

            if (viewModel.shouldDisplayExchange) {
                SmartPaymentInfoItem(
                    modifier = Modifier.padding(top = 24.dp),
                    icon = R.drawable.ic_money_gray,
                    title = stringResource(R.string.payment_amount_bottom_sheet_exchange_type),
                    subtitle = viewModel.amountUIState.exchangeRateLabel,
                    rightTitle = stringResource(R.string.payment_amount_bottom_sheet_amount_to_debit),
                    rightSubtitle = viewModel.amountUIState.convertedAmountLabel,
                    showVerticalDivision = true
                )
            }

            SmartPaymentInfoItem(
                modifier = Modifier.padding(top = 24.dp),
                title = stringResource(R.string.smart_payment_motive),
                icon = R.drawable.ic_notebook_motive,
                subtitle = viewModel.amountUIState.motive
            )

            SmartPaymentInfoItem(
                modifier = Modifier.padding(top = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                icon = R.drawable.ic_calendar,
                title = viewModel.amountUIState.currentDate,
                rightSubtitle = viewModel.amountUIState.currentTime
            )
        }
    }
}
