package com.multimoney.multimoney.presentation.ui.home.product.smart.uisections

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
import com.multimoney.multimoney.presentation.uielement.CustomButtonType.PrimarySecondary

/**
 * CtaButtons: This CtaButtons is used to display Pay and Disbursement options
 *
 * Parameters:
 * @param modifier: Apply style.
 * @param canSendMoney: Enable and disable SendMoney button.
 * @param onClickPay: Action to be executed on click pay button.
 * @param onClickSendMoney: Action to be executed on click SendMoney button.
 * **/

@Composable
fun SmartCtaButtons(
    modifier: Modifier,
    canSendMoney: Boolean,
    onClickPay: () -> Unit = {},
    onClickSendMoney: () -> Unit = {}
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (canSendMoney) {
            CustomButton(
                text = stringResource(string.home_send_money_button_text),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                buttonType = PrimarySecondary,
                onClick = {
                    onClickSendMoney()
                }
            )
            Spacer(Modifier.width(16.dp))
            CustomButton(
                text = stringResource(string.home_pay_button_text),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                onClick = {
                    onClickPay()
                }
            )
        } else {
            CustomButton(
                text = stringResource(string.home_pay_button_text),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                onClick = {
                    onClickPay()
                }
            )
        }
    }
}
