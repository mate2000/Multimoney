package com.multimoney.multimoney.presentation.ui.home.product.crypto

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.multimoney.multimoney.presentation.ui.home.product.crypto.uisections.CryptoActionsSectionContent

/**
 * Composable function to show the option to active crypto product
 */
@Composable
fun CryptoCtaFooterExpanded(
    modifier: Modifier = Modifier,
    isNotEmptyState: Boolean,
    isOutOfService: Boolean,
    isAccountStatusBlocked: Boolean,
    isSendAndGiveEnable: Boolean,
    onBuyActionClicked: () -> Unit,
    onSendActionClicked: () -> Unit,
    onSellActionClicked: () -> Unit,
    onGiveActionClicked: () -> Unit,
) {
    CryptoActionsSectionContent(
        modifier = modifier,
        enableSendAndGive = isSendAndGiveEnable,
        buyButtonEnableCondition = isOutOfService.not() and isAccountStatusBlocked.not(),
        sellButtonEnableCondition = isOutOfService.not() and isAccountStatusBlocked.not() and isNotEmptyState,
        sendButtonEnableCondition = isOutOfService.not() and isNotEmptyState,
        giveButtonEnableCondition = isOutOfService.not() and isAccountStatusBlocked.not(),
        buyAction = onBuyActionClicked,
        sellAction = onSellActionClicked,
        giveAction = onGiveActionClicked,
        sendAction = onSendActionClicked
    )
}
