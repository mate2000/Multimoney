package com.multimoney.multimoney.presentation.ui.crypto.wallet

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.presentation.uielement.TopNavBar

@Composable
fun HomeWallet(
    walletViewModel: HomeWalletViewModel = hiltViewModel(),
    onBackPressed: () -> Unit
) {

    HomeWalletContent(onBackPressed = onBackPressed)
}

@Composable
fun HomeWalletContent(
    onBackPressed: () -> Unit = {},
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopNavBar(
            isLeftButtonVisible = true,
            onLeftButtonClick = onBackPressed
        )
    }
}