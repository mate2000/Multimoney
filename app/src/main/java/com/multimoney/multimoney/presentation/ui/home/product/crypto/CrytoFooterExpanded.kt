package com.multimoney.multimoney.presentation.ui.home.product.crypto

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
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
import com.multimoney.multimoney.presentation.ui.home.product.crypto.uisections.NoticeSection
import kotlinx.coroutines.flow.Flow

@Composable
fun CryptoFooterExpanded(
    balance: Balance?,
    userStatus: ValidateUserStatus?,
    cryptoMovements: Flow<PagingData<CryptoCurrencyMovement>>,
    actionMarket: () -> Unit,
    actionWallet: () -> Unit,
    onShowAllClick: () -> Unit
) {
    if (userStatus?.infoCrypto?.status == CryptoAccountStatus.ACTIVE.status) {
        CryptoFooterExpandedContent(
            balance,
            cryptoMovements,
            actionMarket,
            actionWallet,
            onShowAllClick
        )
    }
}

@Composable
fun CryptoFooterExpandedContent(
    balance: Balance?,
    cryptoMovements: Flow<PagingData<CryptoCurrencyMovement>>,
    actionMarket: () -> Unit,
    actionWallet: () -> Unit,
    onShowAllClick: () -> Unit
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
        ButtonsSection(
            walletEnable = profileEnable,
            actionMarket = actionMarket,
            actionWallet = actionWallet
        )

        if (profileEnable) {
            Column {
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

const val ZERO_MOVEMENTS = 0