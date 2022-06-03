package com.multimoney.multimoney.presentation.uielement

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.presentation.theme.GrayScale600
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.defaultShimmerTheme
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer

@Composable
fun ShimmerView(
    modifier: Modifier
) {

}

fun Modifier.shimmer(
    duration: Int = 800,
    delay: Int = 500
): Modifier = composed {
    val shimmer = rememberShimmer(
        shimmerBounds = ShimmerBounds.View,
        theme = createCustomTheme(duration, delay),
    )
    shimmer(customShimmer = shimmer)
}

private fun createCustomTheme(duration: Int, delay: Int) = defaultShimmerTheme.copy(
    animationSpec = infiniteRepeatable(
        animation = tween(
            durationMillis = duration,
            delayMillis = delay,
            easing = LinearEasing,
        ),
        repeatMode = RepeatMode.Restart,
    ),
    rotation = 30f,
    shaderColors = listOf(
        GrayScale600.copy(alpha = 0.25f),
        GrayScale600.copy(alpha = 1.0f),
        GrayScale600.copy(alpha = 0.25f),
    ),
    shaderColorStops = null,
    shimmerWidth = 100.dp,
)