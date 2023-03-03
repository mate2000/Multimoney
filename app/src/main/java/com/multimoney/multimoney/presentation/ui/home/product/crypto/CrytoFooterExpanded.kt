package com.multimoney.multimoney.presentation.ui.home.product.crypto

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.multimoney.data.util.catalog.CryptoAccountStatus
import com.multimoney.domain.model.balance.Balance
import com.multimoney.domain.model.crypto.CryptoCurrencyMovement
import com.multimoney.domain.model.security.ValidateUserStatus
import com.multimoney.multimoney.presentation.ui.home.product.crypto.uisections.ButtonsSection
import com.multimoney.multimoney.presentation.ui.home.product.crypto.uisections.CryptoCurrencies
import com.multimoney.multimoney.presentation.ui.home.product.crypto.uisections.CryptoMovementsSection
import com.multimoney.multimoney.presentation.ui.home.product.crypto.uisections.MaintenanceSection
import com.multimoney.multimoney.presentation.ui.home.product.crypto.uisections.NoticeSection
import kotlinx.coroutines.flow.Flow

@Composable
fun CryptoFooterExpanded(
    balance: Balance?,
    userStatus: ValidateUserStatus?,
    cryptoMovements: Flow<PagingData<CryptoCurrencyMovement>>,
    registerAdjustEvent: () -> Unit,
    actionMarket: () -> Unit,
    actionWallet: () -> Unit,
    onShowAllClick: () -> Unit,
    onNavigateToReleaseTransaction: ( CryptoCurrencyMovement?) -> Unit
) {
    LaunchedEffect(key1 = true) { registerAdjustEvent() }
    if (userStatus?.infoCrypto?.status == CryptoAccountStatus.ACTIVE.status) {
        CryptoFooterExpandedContent(
            balance,
            cryptoMovements,
            actionMarket,
            actionWallet,
            onShowAllClick,
            onNavigateToReleaseTransaction
        )
    }
}

@Composable
fun CryptoFooterExpandedContent(
    balance: Balance?,
    cryptoMovements: Flow<PagingData<CryptoCurrencyMovement>>,
    actionMarket: () -> Unit,
    actionWallet: () -> Unit,
    onShowAllClick: () -> Unit,
    onNavigateToReleaseTransaction: ( CryptoCurrencyMovement?) -> Unit
) {
    val cryptoCurrencies = balance?.balanceCryptoAccount?.items
    val movements = cryptoMovements.collectAsLazyPagingItems()
    val profileEnable = !cryptoCurrencies.isNullOrEmpty() || movements.itemCount > ZERO_MOVEMENTS

    Column(
        modifier = Modifier
            .padding(top = 16.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalArrangement = Arrangement.Top
    ) {
        val outOfService = balance?.balanceCryptoAccount?.outOfService ?: false
        ButtonsSection(
            walletEnable = profileEnable,
            actionMarket = actionMarket,
            actionWallet = actionWallet
        )
        when {
            profileEnable and outOfService.not() -> Column {
                CryptoCurrencies(
                    items = cryptoCurrencies,
                    viewAllClick = { actionWallet() }
                )
                CryptoMovementsSection(
                    cryptoMovements = movements,
                    onShowAllClick = onShowAllClick,
                    onNavigateToReleaseTransaction = onNavigateToReleaseTransaction
                )
            }
            profileEnable and outOfService -> MaintenanceSection()
            profileEnable.not() and outOfService.not() -> NoticeSection()
            profileEnable.not() and outOfService -> MaintenanceSection()
            else -> NoticeSection()
        }
    }
}

const val ZERO_MOVEMENTS = 0