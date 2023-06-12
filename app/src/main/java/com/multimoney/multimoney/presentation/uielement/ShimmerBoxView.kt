package com.multimoney.multimoney.presentation.uielement

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.presentation.theme.GrayScale600
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.defaultShimmerTheme
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer

/**
 * ShimmerBoxView: to apply the shimmer effect to a skeleton view.
 *
 * Parameters:
 * @param duration: Variable in millisecond which defines the duration fo the animation.
 * @param delay: Variable in millisecond which defines the delay to restart the animation.
 * @param repeatMode: Variable that defines the repeat Mode, there are 2 possible modes [RepeatMode.Restart]
 * and [RepeatMode.Reverse].
 * @param rotation: Float that defines the rotation on the shimmer.
 * @param shimmerWidth: Value in Dp to define the width of the shimmer
 * @param content: Function that contain the skeleton view
 *
 */
@Composable
fun ShimmerBoxView(
    duration: Int = SHIMMER_ANIMATION_DURATION_DEFAULT,
    delay: Int = SHIMMER_ANIMATION_DELAY_DEFAULT,
    repeatMode: RepeatMode = SHIMMER_REPEAT_MODE_DEFAULT,
    rotation: Float = SHIMMER_ROTATION_DEFAULT,
    shimmerWidth: Dp = SHIMMER_WIDTH_DEFAULT,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .background(MultimoneyTheme.colors.background)
            .shimmer(
                duration = duration,
                delay = delay,
                repeatMode = repeatMode,
                rotation = rotation,
                shimmerWidth = shimmerWidth
            )
    ) {
        content.invoke()
    }
}


private fun Modifier.shimmer(
    duration: Int,
    delay: Int,
    repeatMode: RepeatMode,
    rotation: Float,
    shimmerWidth: Dp
): Modifier = composed {
    val shimmer = rememberShimmer(
        shimmerBounds = ShimmerBounds.View,
        theme = createCustomTheme(duration, delay, repeatMode, rotation, shimmerWidth),
    )
    shimmer(customShimmer = shimmer)
}

private fun createCustomTheme(
    duration: Int,
    delay: Int,
    repeatMode: RepeatMode,
    rotation: Float,
    shimmerWidth: Dp
) =
    defaultShimmerTheme.copy(
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = duration,
                delayMillis = delay,
                easing = LinearEasing,
            ),
            repeatMode = repeatMode,
        ),
        rotation = rotation,
        shaderColors = listOf(
            GrayScale600.copy(alpha = 0.25f),
            GrayScale600.copy(alpha = 1.0f),
            GrayScale600.copy(alpha = 0.25f),
        ),
        shaderColorStops = null,
        shimmerWidth = shimmerWidth,
    )

private const val SHIMMER_ANIMATION_DURATION_DEFAULT = 800
private const val SHIMMER_ANIMATION_DELAY_DEFAULT = 500
private val SHIMMER_REPEAT_MODE_DEFAULT = RepeatMode.Restart
private const val SHIMMER_ROTATION_DEFAULT = 30f
private val SHIMMER_WIDTH_DEFAULT = 100.dp
