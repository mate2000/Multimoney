package com.multimoney.multimoney.presentation.ui.smart.transfer.smart.mycontactamount

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.multimoney.data.util.catalog.Brand
import com.multimoney.multimoney.R.drawable
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel.AmountUIEvent.OnNavigateHome
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel.AmountUIEvent.OnShareVoucherImage
import com.multimoney.multimoney.presentation.uielement.PaymentSuccessResult
import com.multimoney.multimoney.presentation.uielement.SmartPaymentInfoItem
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.getMaskedAccount
import com.multimoney.multimoney.presentation.util.getMaskedAccountIban

@Composable
fun MyContactsTransferSuccess(
    viewModel: MyContactsTransferAmountViewModel
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
                string.smart_payment_you_sent,
                viewModel.amountUIState.currency
            ),
            amount = viewModel.getFormattedAmount(),
            exchangedAmount = if (viewModel.shouldDisplayExchange) {
                viewModel.amountUIState.convertedAmountLabel
            } else {
                null
            },
            fromToText = stringResource(string.smart_payment_amount_bottom_sheet_to),
            showButton = false
        ) {
            SmartPaymentInfoItem(
                verticalAlignment = Alignment.CenterVertically,
                icon = viewModel.destinyCurrency?.accountIcon,
                title = stringResource(string.smart_payment_destiny_account_label),
                subtitle = if (Brand.CostaRica.id == viewModel.idBrand) {
                    getMaskedAccountIban(
                        viewModel.phoneAccount?.ibanNumber ?: ""
                    )
                } else {
                    getMaskedAccount(
                        accountNumber = viewModel.phoneAccount?.accountNumber ?: "",
                        prefix = Brand.ElSalvador.countryCode.uppercase()
                    )
                }
            )

            SmartPaymentInfoItem(
                verticalAlignment = Alignment.Top,
                icon = drawable.ic_receipt,
                title = stringResource(string.smart_payment_reference_number_label),
                subtitle = viewModel.amountUIState.referenceNumber
            )

            if (viewModel.shouldDisplayExchange) {
                SmartPaymentInfoItem(
                    icon = drawable.ic_money_gray,
                    title = stringResource(string.payment_amount_bottom_sheet_exchange_type),
                    subtitle = viewModel.amountUIState.exchangeRateLabel,
                    rightTitle = stringResource(string.payment_amount_bottom_sheet_amount_to_debit),
                    rightSubtitle = viewModel.amountUIState.convertedAmountLabel,
                    showVerticalDivision = true
                )
            }

            SmartPaymentInfoItem(
                title = stringResource(string.smart_payment_motive),
                icon = drawable.ic_notebook_motive,
                subtitle = viewModel.amountUIState.motive
            )

            SmartPaymentInfoItem(
                verticalAlignment = Alignment.CenterVertically,
                icon = drawable.ic_calendar,
                title = viewModel.amountUIState.currentDate,
                rightSubtitle = viewModel.amountUIState.currentTime
            )
        }
    }
}
