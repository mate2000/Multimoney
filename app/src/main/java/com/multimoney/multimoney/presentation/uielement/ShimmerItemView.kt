package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme

@Composable
fun ShimmerItemView(modifier: Modifier, startRadius: Dp = 0.dp, endRadius: Dp = 0.dp) {
    Spacer(
        modifier = modifier.background(
            MultimoneyTheme.colors.shimmerItemColor,
            shape = RoundedCornerShape(
                topStart = startRadius,
                bottomStart = startRadius,
                topEnd = endRadius,
                bottomEnd = endRadius
            )
        )
    )
}

@Composable
fun ShimmerItemView(modifier: Modifier, radius: Dp = 0.dp) {
    Spacer(
        modifier = modifier
            .background(MultimoneyTheme.colors.shimmerItemColor, shape = RoundedCornerShape(radius))
    )
}