package com.multimoney.multimoney.presentation.ui.smart.transfer.transfer365.amount

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel.AmountUIEvent.OnNavigateHome
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel.AmountUIEvent.OnShareVoucherImage
import com.multimoney.multimoney.presentation.uielement.PaymentSuccessResult
import com.multimoney.multimoney.presentation.uielement.SmartPaymentInfoItem
import com.multimoney.multimoney.presentation.uielement.TopNavBar

@Composable
fun Transfer365SuccessScreen(viewModel: Transfer365AmountViewModel) {
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
            fromToText = stringResource(R.string.smart_payment_amount_bottom_sheet_to),
            showButton = false
        ) {
            SmartPaymentInfoItem(
                verticalAlignment = Alignment.CenterVertically,
                icon = viewModel.destinyCurrency?.accountIcon,
                title = viewModel.amountUIState.destinyAccountDisplay?.sheetTitle.orEmpty(),
                subtitle = viewModel.amountUIState.destinyAccountDisplay?.sheetSubtitle2
            )

            SmartPaymentInfoItem(
                verticalAlignment = Alignment.Top,
                icon = R.drawable.ic_receipt,
                title = stringResource(R.string.smart_payment_reference_number_label),
                subtitle = viewModel.amountUIState.referenceNumber
            )

            SmartPaymentInfoItem(
                title = stringResource(R.string.smart_payment_motive),
                icon = R.drawable.ic_notebook_motive,
                subtitle = viewModel.amountUIState.motive
            )

            SmartPaymentInfoItem(
                verticalAlignment = Alignment.CenterVertically,
                icon = R.drawable.ic_calendar,
                title = viewModel.amountUIState.currentDate,
                rightSubtitle = viewModel.amountUIState.currentTime
            )
        }
    }
}
