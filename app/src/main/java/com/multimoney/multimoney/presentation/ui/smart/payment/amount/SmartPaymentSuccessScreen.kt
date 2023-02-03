package com.multimoney.multimoney.presentation.ui.smart.payment.amount

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.multimoney.data.util.catalog.Brand
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel.AmountUIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel.AmountUIEvent.OnNavigateHome
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel.AmountUIEvent.OnShareVoucherImage
import com.multimoney.multimoney.presentation.uielement.PaymentSuccessResult
import com.multimoney.multimoney.presentation.uielement.SmartPaymentInfoItem
import com.multimoney.multimoney.presentation.uielement.TopNavBar

@Composable
fun SmartPaymentSuccessScreen(
    viewModel: SavingAmountViewModel
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
                R.string.smart_payment_you_saved_on_your_smart_account,
                viewModel.destinyCurrency?.symbol.orEmpty()
            ),
            amount = viewModel.getFormattedAmount(),
            exchangedAmount = if (viewModel.shouldDisplayExchange) {
                viewModel.amountUIState.convertedAmountLabel
            } else {
                null
            },
            fromToText = stringResource(R.string.smart_payment_from_label),
            buttonText = stringResource(R.string.smart_payment_make_another_payment),
            onButtonClick = { viewModel.onAmountUIEvent(OnNavigateBack) }
        ) {
            SmartPaymentInfoItem(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 0.dp),
                icon = viewModel.amountUIState.originAccountDisplay?.icon ?: 0,
                title = if (viewModel.idBrand == Brand.ElSalvador.id) {
                    stringResource(R.string.smart_payment_card_bank_label)
                } else {
                    stringResource(R.string.smart_payment_origin_account_label)
                },
                subtitle = viewModel.amountUIState.originAccountDisplay?.sheetSubtitle ?: ""
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
                    verticalAlignment = Alignment.CenterVertically,
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
                verticalAlignment = Alignment.CenterVertically,
                icon = R.drawable.ic_calendar,
                title = viewModel.amountUIState.currentDate,
                rightSubtitle = viewModel.amountUIState.currentTime
            )
        }
    }
}
