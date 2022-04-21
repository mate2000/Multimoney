package com.multimoney.multimoney.presentation.uielement

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * StoryProgressBar: This Row is used to match design system
 *
 * Parameters:
 * @param steps: Number of steps to have on the story style progress bar.
 * @param currentStep: Current story step.
 * @param paused: Variable to stop the progress.
 * @param onFinished: Function to update the step.
 * @param backgroundColor: Background color where the progress hasn't reached.
 * @param progressColor: Actual progress color.
 * @param modifier: Apply style.
 * **/
@Composable
fun StoryProgressBar(
    steps: Int,
    currentStep: Int,
    paused: Boolean,
    onFinished: () -> Unit,
    backgroundColor: Color,
    progressColor: Color,
    modifier: Modifier
) {
    val percent = remember { Animatable(0f) }
    LaunchedEffect(paused, currentStep) {
        percent.snapTo(0f)
        percent.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = (10000 * (1f - percent.value)).toInt(),
                easing = LinearEasing
            )
        )
        onFinished()
    }
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        for (index in 1..steps) {
            Row(
                modifier = Modifier
                    .height(4.dp)
                    .clip(RoundedCornerShape(50, 50, 50, 50))
                    .weight(1f)
                    .background(backgroundColor)
            ) {
                Box(
                    modifier = Modifier
                        .background(progressColor)
                        .fillMaxHeight().let {
                            when (index) {
                                currentStep -> it.fillMaxWidth(percent.value)
                                in 0..currentStep -> it.fillMaxWidth(1f)
                                else -> it
                            }
                        },
                )
            }
            if (index != steps) {
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
}