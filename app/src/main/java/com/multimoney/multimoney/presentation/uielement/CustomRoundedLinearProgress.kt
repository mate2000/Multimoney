package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.presentation.theme.DefaultWhite
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme

@Composable
fun CustomRoundedLinearProgress(progress: Float, modifier: Modifier) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(15.dp))
            .wrapContentHeight()
            .fillMaxWidth()
    ) {
        LinearProgressIndicator(modifier = modifier,
            progress = progress,
            backgroundColor = MultimoneyTheme.colors.progressBackground,
            color = MultimoneyTheme.colors.progressPercentage)
    }
}