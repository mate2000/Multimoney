package com.multimoney.multimoney.presentation.ui.test.shimmer

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.presentation.theme.GrayScale300
import com.multimoney.multimoney.presentation.theme.GrayScale600
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.defaultShimmerTheme
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer


@Composable
fun ShimmerTest() {
    Column(
        modifier = Modifier
            .shimmer(duration = 800)
    ) {
        repeat(3) {
            Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                Spacer(
                    modifier = Modifier
                        .size(128.dp)
                        .background(GrayScale300)
                )
                Column(
                    Modifier
                        .padding(start = 18.dp)
                ) {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(20.dp)
                            .background(GrayScale300)
                    )
                    Spacer(
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .fillMaxWidth()
                            .height(20.dp)
                            .background(GrayScale300)

                    )
                    Spacer(
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .fillMaxWidth()
                            .height(20.dp)
                            .background(GrayScale300)
                    )
                }
            }
        }
    }
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
