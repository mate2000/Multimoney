package com.multimoney.multimoney.presentation.ui.home.product.credit.uisections

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.R.drawable
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel.UIEvent.OnShowAutomaticPaymentEdit
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnChipQuotaClick
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToScheduleAutomaticPaymentScreen
import com.multimoney.multimoney.presentation.uielement.CustomTextButton
import com.multimoney.multimoney.presentation.uielement.ScheduleAutomaticPaymentTextInfo
import com.multimoney.multimoney.presentation.util.BAR_DIVIDER_FORMAT_YEAR_TWO_DIGITS
import com.multimoney.multimoney.presentation.util.getCardDateFormat

@Composable
fun ScheduleAutomaticPayment(viewModel: ProductViewModel, sharedViewModel: HomeViewModel) {
    viewModel.balanceCredit?.balanceCredit?.firstOrNull()?.summary?.firstOrNull()?.let { summary ->
        if (summary.automaticDebitEnabled == true) {
            ScheduleAutomaticPaymentTextInfo(
                dateText = getCardDateFormat(
                    viewModel.balanceCredit?.getFirstSummary()?.paymentDateLabel,
                    BAR_DIVIDER_FORMAT_YEAR_TWO_DIGITS
                ),
                chipLeadingIconResource = viewModel.uiState.scheduleChipIconResource,
                amountText = viewModel.getSchedulePaymentAmount(viewModel.balanceCredit),
                chipOnClick = { viewModel.onUIEvent(OnChipQuotaClick) },
                threePointsOnClick = {
                    sharedViewModel.onUIEvent(OnShowAutomaticPaymentEdit)
                }
            )
        } else if (summary.applyAutomaticDebit == true) {
            Spacer(modifier = Modifier.height(24.dp))
            CustomTextButton(
                string.schedule_automatic_payment_credit_button,
                drawable.ic_calendar_schedule_automatic_payment
            ) {
                viewModel.onUIEvent(
                    OnNavigateToScheduleAutomaticPaymentScreen
                )
            }
        }
    }
}
