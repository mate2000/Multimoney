package com.multimoney.multimoney.presentation.ui.home.product.crypto

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.multimoney.domain.model.balance.Account
import com.multimoney.domain.model.balance.Balance
import com.multimoney.multimoney.presentation.ui.home.product.crypto.uisections.CryptoActionsSection

/**
 * Composable function to show the option to active crypto product
 */
@Composable
fun CryptoCtaFooterExpanded(
    balance: Balance?,
    profileEnable: Boolean?,
    noBalanceAction: () -> Unit,
    hasBalanceAction: () -> Unit,
    onSendActionClicked: () -> Unit,
    onSellActionClicked: () -> Unit,
    isCryptoTransferEnabled: Boolean,
) {
    val hasSmartBalance by remember { mutableStateOf(verifyIfHasSmartBalance(balance?.balanceAccountSmart)) }

    CryptoActionsSection(
        hasSmartBalance = hasSmartBalance,
        enableCryptoActions = profileEnable ?: false,
        enableSendAndGive = isCryptoTransferEnabled,
        noBalanceAction = noBalanceAction,
        hasBalanceAction = hasBalanceAction,
        sendAction = onSendActionClicked,
        sellAction = onSellActionClicked
    )
}

fun verifyIfHasSmartBalance(balanceAccountSmart: List<Account?>?): Boolean {
    if (balanceAccountSmart.isNullOrEmpty()) {
        return false
    }
    val balances = balanceAccountSmart.map { it?.totalBalance ?: 0.0 }
    return balances.sum() > 0.0
}
