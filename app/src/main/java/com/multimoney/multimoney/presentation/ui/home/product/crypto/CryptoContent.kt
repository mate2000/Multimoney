package com.multimoney.multimoney.presentation.ui.home.product.crypto

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.multimoney.data.util.catalog.CryptoAccountStatus
import com.multimoney.data.util.catalog.SmartAccountStatus
import com.multimoney.domain.model.balance.BalanceCryptoAccount
import com.multimoney.domain.model.crypto.HistoricalBalanceClient
import com.multimoney.domain.model.security.ValidateUserStatus
import com.multimoney.multimoney.presentation.ui.home.product.crypto.uisections.CryptoCardDiscoverCrypto
import com.multimoney.multimoney.presentation.ui.home.product.crypto.uisections.CryptoCardSmartInProcess
import com.multimoney.multimoney.presentation.ui.home.product.crypto.uisections.CryptoCardWithBalance
import com.multimoney.multimoney.presentation.uielement.CustomProductBackground
import com.multimoney.multimoney.presentation.uielement.ProductBackGroundType

@Composable
fun CryptoContent(
    userStatus: ValidateUserStatus?,
    cryptoBalance: BalanceCryptoAccount?,
    clientBalanceHistory: List<HistoricalBalanceClient>,
    cryptoEmptyState: Boolean,
    openSmartCryptoAction: () -> Unit = {}
) {
    when (userStatus?.infoBankAccount?.status) {
        SmartAccountStatus.EXIST_IN_CORE.status -> {
            when (userStatus.infoCrypto?.status) {
                CryptoAccountStatus.ACTIVE.status -> {
                    if (cryptoBalance == null) {
                        // show balance 0 card
                        CustomProductBackground(
                            modifier = Modifier
                                .padding(horizontal = 16.dp),
                            type = ProductBackGroundType.ComplementaryTwo
                        ) {
                            CryptoCardWithBalance(
                                cryptoBalance = 0.0,
                                isEmptyStateDisable = cryptoEmptyState
                            )
                        }
                        return
                    }
                    // show card with balance and gains/loses
                    CustomProductBackground(
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        type = ProductBackGroundType.ComplementaryTwo
                    ) {
                        CryptoCardWithBalance(
                            cryptoBalance = cryptoBalance.globalBalance ?: 0.0,
                            clientCryptoBalanceHistory = clientBalanceHistory,
                            isEmptyStateDisable = cryptoEmptyState
                        )
                    }
                }
                CryptoAccountStatus.INACTIVE.status -> {
                    // show offer card with action
                    CustomProductBackground(
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        type = ProductBackGroundType.ComplementaryTwo
                    ) {
                        CryptoCardDiscoverCrypto(
                            wording = userStatus.infoCrypto?.wording,
                            onClick = { openSmartCryptoAction.invoke() }
                        )
                    }
                }
            }
        }
        SmartAccountStatus.NO_EXIST.status -> {
            // show offer card with no action when smart is in process
            userStatus.infoBankAccount?.infoRequest?.let {
                if (it.statusRequest != "" && it.idRequestGlobal != 0L && it.currentStep != "") {
                    CustomProductBackground(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        type = ProductBackGroundType.ComplementaryTwo
                    ) {
                        CryptoCardSmartInProcess(
                            wording = userStatus.infoCrypto?.wording,
                            onClick = { openSmartCryptoAction.invoke() }
                        )
                    }
                } else {
                    // show offer card with action
                    CustomProductBackground(
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        type = ProductBackGroundType.ComplementaryTwo
                    ) {
                        CryptoCardDiscoverCrypto(
                            wording = userStatus.infoCrypto?.wording,
                            onClick = { openSmartCryptoAction.invoke() }
                        )
                    }
                }
            }
        }
    }
}
