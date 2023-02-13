package com.multimoney.multimoney.presentation.ui.crypto

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.presentation.uielement.ShimmerBoxView
import com.multimoney.multimoney.presentation.uielement.ShimmerItemView

@Composable
fun CurrencyTitleSectionSkeleton() {
    ShimmerBoxView {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ShimmerItemView(
                modifier = Modifier
                    .width(164.dp)
                    .height(24.dp),
                radius = 8.dp
            )
        }
    }
}

@Composable
fun CurrencyTitleConfirmationSectionSkeleton() {
    ShimmerBoxView {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ShimmerItemView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp),
                radius = 1.dp
            )
        }
    }
}

@Composable
fun VoucherCurrencyExchangeInfoSkeleton() {
    ShimmerBoxView {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ShimmerItemView(
                modifier = Modifier
                    .width(320.dp)
                    .height(48.dp),
                radius = 8.dp
            )
        }
    }
}