package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme

@Composable
fun CustomRoundedLinearProgress(
    progress: Float,
    modifier: Modifier,
    progressBackground: Color = MultimoneyTheme.colors.progressBackground,
    gradientColorStops: Array<Pair<Float, Color>> = arrayOf(
        0.0f to MultimoneyTheme.colors.linearProgressIndicatorStart,
        0.65f to MultimoneyTheme.colors.linearProgressIndicatorFinal
    )
) {
    Canvas(
        modifier = modifier,
        onDraw = {
            // Calculating width of foreground indicator
            val calculatedProgress = progress * size.width
            // Drawing the background of the linear progress bar
            drawLine(
                color = progressBackground,
                cap = StrokeCap.Round,
                strokeWidth = size.height,
                start = Offset(x = 0f, y = 0f),
                end = Offset(x = size.width, y = 0f)
            )
            // Drawing the linear progress bar with gradient
            drawLine(
                brush = Brush.horizontalGradient(
                    colorStops = gradientColorStops,
                    startX = 0f,
                    endX = calculatedProgress
                ),
                cap = StrokeCap.Round,
                strokeWidth = size.height,
                start = Offset(x = 0f, y = 0f),
                end = Offset(x = calculatedProgress, y = 0f)
            )
        }
    )
}