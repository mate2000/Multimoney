package com.multimoney.multimoney.presentation.ui.home.product.crypto

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.CryptoAccountStatus
import com.multimoney.domain.model.balance.Account
import com.multimoney.domain.model.balance.Balance
import com.multimoney.domain.model.crypto.CryptoCurrencyMovement
import com.multimoney.domain.model.security.ValidateUserStatus
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.ui.home.product.crypto.uisections.ButtonsSection
import com.multimoney.multimoney.presentation.ui.home.product.crypto.uisections.CryptoActionsSection
import com.multimoney.multimoney.presentation.ui.home.product.crypto.uisections.CryptoCurrencies
import com.multimoney.multimoney.presentation.ui.home.product.crypto.uisections.CryptoMovementsSection
import com.multimoney.multimoney.presentation.ui.home.product.crypto.uisections.NoticeSection
import kotlinx.coroutines.flow.Flow

@Composable
fun CryptoFooterExpanded(
    balance: Balance?,
    userStatus: ValidateUserStatus?,
    idBrand: String,
    cryptoMovements: Flow<PagingData<CryptoCurrencyMovement>>,
    actionMarket: () -> Unit,
    actionWallet: () -> Unit,
    noBalanceAction: () -> Unit,
    hasBalanceAction: () -> Unit,
    onShowAllClick: () -> Unit
) {
    if (userStatus?.infoCrypto?.status == CryptoAccountStatus.ACTIVE.status) {
        CryptoFooterExpandedContent(
            balance,
            idBrand,
            cryptoMovements,
            actionMarket,
            actionWallet,
            noBalanceAction,
            hasBalanceAction,
            onShowAllClick
        )
    }
}

@Composable
fun CryptoFooterExpandedContent(
    balance: Balance?,
    idBrand: String,
    cryptoMovements: Flow<PagingData<CryptoCurrencyMovement>>,
    actionMarket: () -> Unit,
    actionWallet: () -> Unit,
    noBalanceAction: () -> Unit,
    hasBalanceAction: () -> Unit,
    onShowAllClick: () -> Unit
) {
    val smartBalanceAvailable = verifyIfHasSmartBalance(balance?.balanceAccountSmart)
    val hasSmartBalance by remember { mutableStateOf(smartBalanceAvailable) }
    val cryptoCurrencies = balance?.balanceCryptoAccount?.items
    val movements = cryptoMovements.collectAsLazyPagingItems()
    val profileEnable = !cryptoCurrencies.isNullOrEmpty() || movements.itemCount > ZERO_MOVEMENTS

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        backgroundColor = MultimoneyTheme.colors.background,
        bottomBar = {
            val enableSendAndGive = idBrand.toIntOrNull() == Brand.CostaRica.id

            CryptoActionsSection(
                hasSmartBalance = hasSmartBalance,
                enableCryptoActions = profileEnable,
                enableSendAndGive = enableSendAndGive,
                noBalanceAction = noBalanceAction,
                hasBalanceAction = hasBalanceAction
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier.padding(paddingValues),
            verticalArrangement = Arrangement.Top
        ) {
            ButtonsSection(
                walletEnable = profileEnable,
                actionMarket = actionMarket,
                actionWallet = actionWallet
            )
            profileEnable.let {
                if (it) {
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    ) {
                        CryptoCurrencies(
                            items = cryptoCurrencies,
                            itemClick = {},
                            viewAllClick = { actionWallet() }
                        )
                        CryptoMovementsSection(
                            cryptoMovements = movements,
                            onShowAllClick = onShowAllClick
                        )
                    }
                } else {
                    NoticeSection()
                }
            }
        }
    }
}

fun verifyIfHasSmartBalance(balanceAccountSmart: List<Account?>?): Boolean {
    if (balanceAccountSmart.isNullOrEmpty()) {
        return false
    }
    val balances = balanceAccountSmart.map { it?.totalBalance ?: 0.0 }
    return balances.sum() > 0.0
}

private const val ZERO_MOVEMENTS = 0