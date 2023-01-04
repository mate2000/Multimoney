package com.multimoney.multimoney.presentation.ui.smart.payment.amount

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.multimoney.data.util.catalog.Brand
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnNavigateHome
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnShareVoucherImage
import com.multimoney.multimoney.presentation.uielement.PaymentSuccessResult
import com.multimoney.multimoney.presentation.uielement.SmartPaymentInfoItem
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.CARD_NUMBER_LAST_DIGITS
import com.multimoney.multimoney.presentation.util.getMaskedAccountIban

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
                R.string.smart_payment_you_saved_on_your_smart_account,
                viewModel.uiState.currency
            ),
            amount = viewModel.getFormattedAmount(),
            fromToText = stringResource(R.string.smart_payment_from_label),
            buttonText = stringResource(R.string.smart_payment_make_another_payment),
            onButtonClick = { viewModel.onUIEvent(OnNavigateBack) }
        ) {
            SmartPaymentInfoItem(
                verticalAlignment = Alignment.CenterVertically,
                icon = viewModel.editAmountHelper.originIcon,
                title = stringResource(viewModel.editAmountHelper.originTitle),
                subtitle = if (viewModel.editAmountHelper.idBrand == Brand.ElSalvador.id) {
                    stringResource(
                        R.string.visa_card_masked_number,
                        viewModel.editAmountHelper.maskedCardNumber.takeLast(
                            CARD_NUMBER_LAST_DIGITS
                        )
                    )
                } else {
                    getMaskedAccountIban(
                        viewModel.editAmountHelper.maskedCardNumber,
                        stringResource(id = R.string.payment_account_masked_text)
                    )
                }
            )

            SmartPaymentInfoItem(
                verticalAlignment = Alignment.Top,
                icon = R.drawable.ic_receipt,
                title = stringResource(R.string.smart_payment_reference_number_label),
                subtitle = viewModel.uiState.referenceNumber
            )

            if (viewModel.editAmountHelper.shouldDisplayExchange) {
                SmartPaymentInfoItem(
                    verticalAlignment = Alignment.CenterVertically,
                    icon = R.drawable.ic_money_gray,
                    title = stringResource(R.string.payment_amount_bottom_sheet_exchange_type),
                    subtitle = viewModel.uiState.exchangeRateLabel,
                    rightTitle = stringResource(R.string.payment_amount_bottom_sheet_amount_to_debit),
                    rightSubtitle = viewModel.getConvertedAmountFormatted(),
                    showVerticalDivision = true
                )
            }

            SmartPaymentInfoItem(
                verticalAlignment = Alignment.CenterVertically,
                icon = R.drawable.ic_calendar,
                title = viewModel.uiState.currentDate,
                rightSubtitle = viewModel.uiState.currentTime
            )
        }
    }
}
