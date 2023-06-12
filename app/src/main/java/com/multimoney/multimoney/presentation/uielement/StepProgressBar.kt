package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.presentation.theme.WhiteTransparency20
import com.multimoney.multimoney.presentation.theme.WhiteTransparency70

/**
 * StepProgressBar: Display the current step in a process
 *
 * Parameters:
 * @param modifier: Modify StepProgressBar position.
 * @param steps: Total steps flow.
 * @param currentStep: Current step flow.
 */

@Composable
fun StepProgressBar(
    modifier: Modifier = Modifier,
    steps: Int,
    currentStep: Int
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        for (index in 1..steps) {
            Row(
                modifier = Modifier
                    .height(6.dp)
                    .clip(RoundedCornerShape(50, 50, 50, 50))
                    .weight(1f)
                    .background(WhiteTransparency20)
            ) {
                val boxColor = if (index < currentStep || index == currentStep) {
                    WhiteTransparency70
                } else {
                    WhiteTransparency20
                }

                Box(
                    modifier = Modifier
                        .background(boxColor)
                        .fillMaxHeight()
                        .fillMaxWidth(),
                )
            }
            if (index != steps) {
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
}