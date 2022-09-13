package com.multimoney.multimoney.presentation.ui.home.product.skeleton

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.presentation.uielement.ShimmerBoxView
import com.multimoney.multimoney.presentation.uielement.ShimmerItemView

@Composable
@Preview(widthDp = 360, heightDp = 800)
fun ProductScreenSkeleton() {
    ShimmerBoxView {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 20.dp, start = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    ShimmerItemView(modifier = Modifier.size(width = 95.dp, height = 24.dp))
                    ShimmerItemView(
                        modifier = Modifier
                            .size(width = 141.dp, height = 34.dp)
                            .padding(top = 4.dp)
                    )
                }
                Row(Modifier.padding(end = 16.dp)) {
                    ShimmerItemView(modifier = Modifier.size(width = 28.dp, height = 28.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    ShimmerItemView(
                        modifier = Modifier
                            .size(width = 28.dp, height = 28.dp)
                    )
                }
            }
            Row(modifier = Modifier.padding(top = 16.dp)) {
                ShimmerItemView(modifier = Modifier.size(width = 152.dp, height = 141.dp), radius = 24.dp)
                ShimmerItemView(
                    Modifier
                        .size(width = 152.dp, height = 141.dp)
                        .padding(start = 12.dp),
                    radius = 24.dp
                )
                ShimmerItemView(
                    Modifier
                        .size(width = 80.dp, height = 141.dp)
                        .padding(start = 12.dp),
                    startRadius = 24.dp
                )
            }
            ShimmerItemView(
                Modifier
                    .size(width = 192.dp, height = 24.dp)
                    .padding(top = 24.dp)
            )
            ShimmerItemView(
                Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(top = 12.dp, end = 16.dp),
                radius = 24.dp
            )
            ShimmerItemView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .padding(top = 32.dp, end = 16.dp),
                radius = 24.dp
            )
        }
    }
}