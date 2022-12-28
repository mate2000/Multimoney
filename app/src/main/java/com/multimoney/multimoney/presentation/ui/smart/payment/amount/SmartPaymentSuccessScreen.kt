package com.multimoney.multimoney.presentation.ui.smart.payment.amount

import android.view.View
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
import com.multimoney.multimoney.R.drawable
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnNavigateHome
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnShareVoucherImage
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
                string.smart_payment_you_saved_on_your_smart_account,
                viewModel.uiState.currency
            ),
            amount = viewModel.uiState.currency +
                viewModel.uiState.currentAmountValueString.collectAsState().value,
            fromToText = stringResource(string.smart_payment_from_label),
            buttonText = stringResource(string.smart_payment_make_another_payment),
            onButtonClick = { viewModel.onUIEvent(OnNavigateBack) }
        ) {
            SmartPaymentInfoItem(
                modifier = Modifier.padding(start = 21.dp, top = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                icon = drawable.ic_visa_card_item,
                iconModifier = Modifier
                    .height(24.dp)
                    .width(24.dp),
                title = stringResource(string.smart_payment_card_bank_label),
                subtitle = stringResource(
                    string.visa_card_masked_number,
                    viewModel.maskedCardNumber.takeLast(4)
                )
            )

            SmartPaymentInfoItem(
                modifier = Modifier.padding(start = 21.dp, top = 32.dp),
                verticalAlignment = Alignment.Top,
                icon = drawable.ic_receipt,
                iconModifier = Modifier
                    .height(24.dp)
                    .width(24.dp),
                title = stringResource(string.smart_payment_reference_number_label),
                subtitle = viewModel.uiState.referenceNumber
            )

            SmartPaymentInfoItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 21.dp, top = 32.dp, end = 32.dp),
                verticalAlignment = Alignment.CenterVertically,
                icon = drawable.ic_calendar,
                iconModifier = Modifier
                    .height(24.dp)
                    .width(24.dp),
                title = viewModel.uiState.currentDate,
                rightSubtitle = viewModel.uiState.currentTime
            )
        }
    }
}
