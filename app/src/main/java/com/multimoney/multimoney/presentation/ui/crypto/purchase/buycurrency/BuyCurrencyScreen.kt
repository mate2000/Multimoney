package com.multimoney.multimoney.presentation.ui.crypto.purchase.buycurrency

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.presentation.ui.crypto.purchase.PurchaseCryptoSharedViewModel

@Composable
fun BuyCurrencyScreen(
    sharedViewModel: PurchaseCryptoSharedViewModel = hiltViewModel(),
    viewModel: BuyCurrencyScreenViewModel = hiltViewModel()
) {

    Column {
        Text(text = "BuyCurrencyScreen")
    }
}