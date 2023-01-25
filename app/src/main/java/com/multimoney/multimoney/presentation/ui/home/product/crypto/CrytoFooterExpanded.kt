package com.multimoney.multimoney.presentation.ui.home.product.crypto

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import com.multimoney.data.util.catalog.CryptoAccountStatus
import com.multimoney.domain.model.balance.Balance
import com.multimoney.domain.model.crypto.CryptoCurrencyMovement
import com.multimoney.domain.model.security.ValidateUserStatus
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.ui.home.product.crypto.uisections.ButtonsSection
import com.multimoney.multimoney.presentation.ui.home.product.crypto.uisections.CryptoCurrencies
import com.multimoney.multimoney.presentation.ui.home.product.crypto.uisections.CryptoMovementsSection
import com.multimoney.multimoney.presentation.ui.home.product.crypto.uisections.NoticeSection
import kotlinx.coroutines.flow.Flow

@Composable
fun CryptoFooterExpanded(
    balance: Balance?,
    footerExpandedHeight: Float = 0f,
    userStatus: ValidateUserStatus?,
    cryptoMovements: Flow<PagingData<CryptoCurrencyMovement>>,
    actionMarket: () -> Unit,
    actionWallet: () -> Unit,
    onShowAllClick: () -> Unit,
    onFooterExpandedHeightValueChange: (Float) -> Unit
) {
    if (userStatus?.infoCrypto?.status == CryptoAccountStatus.ACTIVE.status) {
        CryptoFooterExpandedContent(
            balance,
            footerExpandedHeight,
            userStatus.infoCrypto?.profileEnable,
            cryptoMovements,
            actionMarket,
            actionWallet,
            onShowAllClick,
            onFooterExpandedHeightValueChange
        )
    }
}

@Composable
fun CryptoFooterExpandedContent(
    balance: Balance?,
    footerExpandedHeight: Float = 0f,
    profileEnable: Boolean?,
    cryptoMovements: Flow<PagingData<CryptoCurrencyMovement>>,
    actionMarket: () -> Unit,
    actionWallet: () -> Unit,
    onShowAllClick: () -> Unit,
    onFooterExpandedHeightValueChange: (Float) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().wrapContentHeight().background(MultimoneyTheme.colors.background)
            .padding(16.dp)
//            .onSizeChanged { size ->
//                if (footerExpandedHeight != size.height.toFloat()) {
//                    onFooterExpandedHeightValueChange(size.height.toFloat())
//                }
//            }
        ,
        verticalArrangement = Arrangement.Top
    ) {
        ButtonsSection(
            walletEnable = profileEnable ?: false,
            actionMarket = actionMarket,
            actionWallet = actionWallet
        )
        profileEnable?.let {
            if (!it) {
                NoticeSection()
            } else {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    CryptoCurrencies(
                        items = balance?.balanceCryptoAccount?.items,
                        itemClick = {},
                        viewAllClick = { actionWallet() }
                    )
                    CryptoMovementsSection(
                        cryptoMovements = cryptoMovements,
                        onShowAllClick = onShowAllClick
                    )
                }
            }
        }
    }
}
