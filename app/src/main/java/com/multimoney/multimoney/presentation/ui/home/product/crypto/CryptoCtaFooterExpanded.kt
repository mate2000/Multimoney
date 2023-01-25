package com.multimoney.multimoney.presentation.ui.home.product.crypto

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import com.multimoney.data.util.catalog.Brand.CostaRica
import com.multimoney.domain.model.balance.Account
import com.multimoney.domain.model.balance.Balance
import com.multimoney.multimoney.presentation.ui.home.product.crypto.uisections.CryptoActionsSection

/**
 * Composable function to show the option to active crypto product
 */
@Composable
fun CryptoCtaFooterExpanded(
    idBrand: String,
    balance: Balance?,
    profileEnable: Boolean?,
    noBalanceAction: () -> Unit,
    hasBalanceAction: () -> Unit,
    ctaFooterExpandedHeight: Float = 0f,
    onCtaFooterExpandedHeightValueChange: (Float) -> Unit
) {
    val hasSmartBalance by remember { mutableStateOf(verifyIfHasSmartBalance(balance?.balanceAccountSmart)) }

    CryptoActionsSection(
        modifier = Modifier
//            .onSizeChanged { size ->
//            if (ctaFooterExpandedHeight != size.height.toFloat()) {
//                onCtaFooterExpandedHeightValueChange(size.height.toFloat())
//            }
//        }
        ,
        hasSmartBalance = hasSmartBalance,
        enableCryptoActions = profileEnable ?: false,
        enableSendAndGive = idBrand.toIntOrNull() == CostaRica.id,
        noBalanceAction = noBalanceAction,
        hasBalanceAction = hasBalanceAction
    )
}

fun verifyIfHasSmartBalance(balanceAccountSmart: List<Account?>?): Boolean {
    if (balanceAccountSmart.isNullOrEmpty()) {
        return false
    }
    val balances = balanceAccountSmart.map { it?.totalBalance ?: 0.0 }
    return balances.sum() > 0.0
}
