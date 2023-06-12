package com.multimoney.multimoney.presentation.ui.crypto.market.currencydetails

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
fun MarketCurrencyDetailsScreenSkeleton() {
    ShimmerBoxView {
        Column {
            ShimmerItemView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(128.dp)
                    .padding(horizontal = 8.dp, vertical = 16.dp),
                radius = 8.dp
            )
        }
    }
}