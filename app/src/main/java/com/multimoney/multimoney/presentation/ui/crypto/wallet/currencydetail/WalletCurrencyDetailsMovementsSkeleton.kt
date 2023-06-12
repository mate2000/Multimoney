package com.multimoney.multimoney.presentation.ui.crypto.wallet.currencydetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.presentation.uielement.ShimmerBoxView
import com.multimoney.multimoney.presentation.uielement.ShimmerItemView

@Composable
fun WalletCurrencyDetailsMovementsSkeleton() {

    ShimmerBoxView {
        Column {
            repeat(4) {
                ShimmerItemView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(vertical = 4.dp),
                    radius = 1.dp
                )
            }
        }
    }
}