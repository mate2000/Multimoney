package com.multimoney.multimoney.presentation.ui.smart.transfer.smart.amount

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel.BaseUIEvent.OnNavigateHome
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel.BaseUIEvent.OnShareVoucherImage
import com.multimoney.multimoney.presentation.uielement.PaymentSuccessResult
import com.multimoney.multimoney.presentation.uielement.SmartPaymentInfoItem
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.getMaskedAccountIban

@Composable
fun OwnTransferSuccessScreen(
    viewModel: OwnTransferAmountViewModel
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
                viewModel.onBaseUIEvent(OnNavigateHome)
            }
        )
        PaymentSuccessResult(
            onShareClick = { view, bounds ->
                viewModel.onBaseUIEvent(
                    OnShareVoucherImage(view, bounds)
                )
            },
            savePayText = stringResource(
                R.string.smart_payment_you_sent,
                viewModel.baseUIState.currency
            ),
            amount = viewModel.getFormattedAmount(),
            exchangedAmount = viewModel.baseUIState.convertedAmountLabel,
            fromToText = stringResource(R.string.smart_payment_to_account),
            showButton = false
        ) {
            SmartPaymentInfoItem(
                verticalAlignment = Alignment.CenterVertically,
                icon = viewModel.baseUIState.originAccountDisplay?.icon ?: 0,
                title = stringResource(R.string.smart_payment_origin_account_label),
                subtitle = getMaskedAccountIban(
                    viewModel.smartAccount?.ibanAccountNumber ?: ""
                )
            )

            SmartPaymentInfoItem(
                verticalAlignment = Alignment.Top,
                icon = R.drawable.ic_receipt,
                title = stringResource(R.string.smart_payment_reference_number_label),
                subtitle = viewModel.baseUIState.referenceNumber
            )

            if (viewModel.shouldDisplayExchange) {
                SmartPaymentInfoItem(
                    icon = R.drawable.ic_money_gray,
                    title = stringResource(R.string.payment_amount_bottom_sheet_exchange_type),
                    subtitle = viewModel.baseUIState.exchangeRateLabel,
                    rightTitle = stringResource(R.string.payment_amount_bottom_sheet_debited_amount),
                    rightSubtitle = viewModel.baseUIState.convertedAmountLabel,
                    showVerticalDivision = true
                )
            }

            SmartPaymentInfoItem(
                title = stringResource(R.string.smart_payment_motive),
                icon = R.drawable.ic_notebook_motive,
                subtitle = viewModel.baseUIState.motive
            )

            SmartPaymentInfoItem(
                verticalAlignment = Alignment.CenterVertically,
                icon = R.drawable.ic_calendar,
                title = viewModel.baseUIState.currentDate,
                rightSubtitle = viewModel.baseUIState.currentTime
            )
        }
    }
}
