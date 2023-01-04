package com.multimoney.multimoney.presentation.ui.smart.transfer.iban.amount

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.amount.SmartTransferAmountViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.amount.SmartTransferAmountViewModel.UIEvent.OnNavigateHome
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.amount.SmartTransferAmountViewModel.UIEvent.OnShareVoucherImage
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
            amount = viewModel.getFormattedAmount(),
            exchangedAmount = viewModel.uiState.convertedAmountLabel,
            fromToText = stringResource(R.string.smart_payment_to_account),
            buttonText = stringResource(R.string.smart_payment_make_another_payment),
            onButtonClick = { viewModel.onUIEvent(OnNavigateBack) }
        ) {
            SmartPaymentInfoItem(
                verticalAlignment = Alignment.CenterVertically,
                icon = R.drawable.ic_bank_account,
                title = stringResource(R.string.smart_payment_origin_account_label),
                subtitle = getMaskedAccountIban(
                    viewModel.editAmountHelper.maskedCardNumber,
                    stringResource(R.string.payment_account_masked_text)
                )
            )

            SmartPaymentInfoItem(
                verticalAlignment = Alignment.Top,
                icon = R.drawable.ic_receipt,
                title = stringResource(R.string.smart_payment_reference_number_label),
                subtitle = viewModel.uiState.referenceNumber
            )

            if (viewModel.editAmountHelper.shouldDisplayExchange) {
                SmartPaymentInfoItem(
                    icon = R.drawable.ic_money_gray,
                    title = stringResource(R.string.payment_amount_bottom_sheet_exchange_type),
                    subtitle = viewModel.uiState.exchangeRateLabel,
                    rightTitle = stringResource(R.string.payment_amount_bottom_sheet_amount_to_debit),
                    rightSubtitle = viewModel.uiState.convertedAmountLabel,
                    showVerticalDivision = true
                )
            }

            SmartPaymentInfoItem(
                title = stringResource(R.string.smart_payment_motive),
                icon = R.drawable.ic_notebook_motive,
                subtitle = viewModel.uiState.motive
            )

            SmartPaymentInfoItem(
                verticalAlignment = Alignment.CenterVertically,
                icon = R.drawable.ic_calendar,
                title = viewModel.uiState.currentDate,
                rightSubtitle = viewModel.uiState.currentTime
            )
        }
    }
}
