package com.multimoney.multimoney.presentation.ui.crypto.wallet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.presentation.uielement.ShimmerBoxView
import com.multimoney.multimoney.presentation.uielement.ShimmerItemView

@Composable
fun WalletSkeleton() {

    ShimmerBoxView {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            ShimmerItemView(
                modifier = Modifier
                    .size(width = 140.dp, height = 56.dp)
                    .padding(top = 4.dp, bottom = 8.dp)
            )
            ShimmerItemView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 8.dp, vertical = 16.dp),
                radius = 24.dp
            )
            ShimmerItemView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(88.dp)
                    .padding(horizontal = 8.dp, vertical = 16.dp),
                radius = 24.dp
            )
            ShimmerItemView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(88.dp)
                    .padding(horizontal = 8.dp, vertical = 16.dp),
                radius = 24.dp
            )
            ShimmerItemView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(88.dp)
                    .padding(horizontal = 8.dp, vertical = 16.dp),
                radius = 24.dp
            )
        }
    }
}