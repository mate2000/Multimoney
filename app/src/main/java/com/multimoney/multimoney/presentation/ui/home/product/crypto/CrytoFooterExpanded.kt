package com.multimoney.multimoney.presentation.ui.home.product.crypto

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.CryptoAccountStatus
import com.multimoney.domain.model.balance.Account
import com.multimoney.domain.model.balance.Balance
import com.multimoney.domain.model.security.ValidateUserStatus
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.ui.home.product.crypto.uisections.ButtonsSection
import com.multimoney.multimoney.presentation.ui.home.product.crypto.uisections.CryptoActionsSection
import com.multimoney.multimoney.presentation.ui.home.product.crypto.uisections.NoticeSection

@Composable
fun CryptoFooterExpanded(
    balance: Balance?,
    userStatus: ValidateUserStatus?,
    idBrand: String,
    actionMarket: () -> Unit,
    actionWallet: () -> Unit,
    noBalanceAction: () -> Unit,
    hasBalanceAction: () -> Unit
) {

    if (userStatus?.infoCrypto?.status == CryptoAccountStatus.ACTIVE.status) {
        CryptoFooterExpandedContent(
            balance,
            userStatus.infoCrypto?.profileEnable,
            idBrand,
            actionMarket,
            actionWallet,
            noBalanceAction,
            hasBalanceAction
        )
    }
}

@Composable
fun CryptoFooterExpandedContent(
    balance: Balance?,
    profileEnable: Boolean?,
    idBrand: String,
    actionMarket: () -> Unit,
    actionWallet: () -> Unit,
    noBalanceAction: () -> Unit,
    hasBalanceAction: () -> Unit
) {

    val smartBalanceAvailable = verifyIfHasSmartBalance(balance?.balanceAccountSmart)
    val hasSmartBalance by remember { mutableStateOf(smartBalanceAvailable) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        backgroundColor = MultimoneyTheme.colors.background,
        bottomBar = {
            val enableSendAndGive = idBrand.toInt() == Brand.CostaRica.id

            CryptoActionsSection(
                hasSmartBalance = hasSmartBalance,
                enableCryptoActions = profileEnable ?: false,
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
                walletEnable = profileEnable ?: false,
                actionMarket = actionMarket,
                actionWallet = actionWallet
            )
            profileEnable?.let {
                if (!it) {
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
