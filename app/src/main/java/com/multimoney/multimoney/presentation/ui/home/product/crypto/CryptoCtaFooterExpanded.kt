package com.multimoney.multimoney.presentation.ui.home.product.crypto

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.multimoney.data.util.catalog.Brand.CostaRica
import com.multimoney.domain.model.balance.Account
import com.multimoney.domain.model.balance.Balance
import com.multimoney.domain.model.crypto.CryptoCurrencyMovement
import com.multimoney.multimoney.presentation.ui.home.product.crypto.uisections.CryptoActionsSection
import kotlinx.coroutines.flow.Flow

/**
 * Composable function to show the option to active crypto product
 */
@Composable
fun CryptoCtaFooterExpanded(
    idBrand: String,
    balance: Balance?,
    cryptoMovements: Flow<PagingData<CryptoCurrencyMovement>>,
    noBalanceAction: () -> Unit,
    hasBalanceAction: () -> Unit,
    onSendActionClicked: () -> Unit,
) {
    val hasSmartBalance by remember { mutableStateOf(verifyIfHasSmartBalance(balance?.balanceAccountSmart)) }
    val cryptoCurrencies = balance?.balanceCryptoAccount?.items
    val movements = cryptoMovements.collectAsLazyPagingItems()
    val profileEnable = !cryptoCurrencies.isNullOrEmpty() || movements.itemCount > ZERO_MOVEMENTS

    CryptoActionsSection(
        hasSmartBalance = hasSmartBalance,
        enableCryptoActions = profileEnable,
        enableSendAndGive = idBrand.toIntOrNull() == CostaRica.id,
        noBalanceAction = noBalanceAction,
        hasBalanceAction = hasBalanceAction,
        sendAction = onSendActionClicked,
    )
}

fun verifyIfHasSmartBalance(balanceAccountSmart: List<Account?>?): Boolean {
    if (balanceAccountSmart.isNullOrEmpty()) {
        return false
    }
    val balances = balanceAccountSmart.map { it?.totalBalance ?: 0.0 }
    return balances.sum() > 0.0
}
