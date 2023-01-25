package com.multimoney.multimoney.presentation.ui.home.product.credit.uisections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType.PrimaryPrimary
import com.multimoney.multimoney.presentation.uielement.CustomButtonType.PrimarySecondary

/**
 * CtaButtons: This CtaButtons is used to display Pay and Disbursement options
 *
 * Parameters:
 * @param modifier: Apply style.
 * @param canDisburse: Enable and disable disbursement button.
 * @param onClickPay: Action to be executed on click pay button.
 * @param onClickDisbursement: Action to be executed on click disbursement button.
 * **/

@Composable
fun CreditCtaButtons(
    modifier: Modifier,
    canDisburse: Boolean,
    paymentAvailable: Boolean,
    onClickPay: () -> Unit = {},
    onClickDisbursement: () -> Unit = {}
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Check if user has payments available
        if (paymentAvailable) {
            CustomButton(
                text = stringResource(string.home_pay_fee_button_text),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                buttonType = if (canDisburse) PrimarySecondary else PrimaryPrimary,
                onClick = {
                    onClickPay()
                }
            )
        }
        // Add a Spacer element if user has payments available and
        // can disburse
        if (paymentAvailable && canDisburse) {
            Spacer(Modifier.width(16.dp))
        }
        // Check if user can disburse
        if (canDisburse) {
            CustomButton(
                text = stringResource(string.home_disburse_button_text),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                onClick = {
                    onClickDisbursement()
                }
            )
        }
    }
}
