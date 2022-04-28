package com.multimoney.multimoney.presentation.uielement

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    var previousStep by remember { mutableStateOf(0) }
    LaunchedEffect(paused, currentStep) {
        if (paused) {
            percent.stop()
        } else {
            if (!paused && currentStep != previousStep) {
                previousStep = currentStep
                percent.snapTo(0f)
            }
            percent.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = (10000 * (1f - percent.value)).toInt(),
                    easing = LinearEasing
                )
            )
            onFinished()
        }
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
                            when {
                                index == currentStep && previousStep == currentStep ->
                                    it.fillMaxWidth(percent.value)
                                index < currentStep ->
                                    it.fillMaxWidth(1f)
                                else ->
                                    it.fillMaxWidth(0f)
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