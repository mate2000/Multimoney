package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.R.drawable
import com.multimoney.multimoney.presentation.theme.ComplementaryOne500
import com.multimoney.multimoney.presentation.theme.ComplementaryTwo500
import com.multimoney.multimoney.presentation.theme.GradientComplementaryOne
import com.multimoney.multimoney.presentation.theme.GradientComplementaryTwo
import com.multimoney.multimoney.presentation.theme.GradientPrimary
import com.multimoney.multimoney.presentation.theme.GradientSecondary
import com.multimoney.multimoney.presentation.theme.GradientTertiary
import com.multimoney.multimoney.presentation.theme.Primary500
import com.multimoney.multimoney.presentation.theme.Secondary500
import com.multimoney.multimoney.presentation.theme.Tertiary500
import com.multimoney.multimoney.presentation.uielement.ProductBackGroundType.ComplementaryOne
import com.multimoney.multimoney.presentation.uielement.ProductBackGroundType.ComplementaryTwo
import com.multimoney.multimoney.presentation.uielement.ProductBackGroundType.Primary
import com.multimoney.multimoney.presentation.uielement.ProductBackGroundType.Secondary
import com.multimoney.multimoney.presentation.uielement.ProductBackGroundType.Tertiary

private const val SIXTY_PERCENT = 0.60

@Composable
fun CustomProductBackground(
    modifier: Modifier,
    onClick: () -> Unit = {},
    type: ProductBackGroundType = Primary,
    content: @Composable () -> Unit
) {

    val configuration = LocalConfiguration.current

    val screenWidth = configuration.screenWidthDp.dp
    val startOffset = screenWidth.value * SIXTY_PERCENT

    val gradientOneColor: Color
    val gradientTwoColor: Color

    when (type) {
        Primary -> {
            if (isSystemInDarkTheme()) {
                gradientOneColor = Primary500
                gradientTwoColor = GradientPrimary
            } else {
                gradientOneColor = Primary500
                gradientTwoColor = GradientPrimary
            }
        }
        Secondary -> {
            if (isSystemInDarkTheme()) {
                gradientOneColor = Secondary500
                gradientTwoColor = GradientSecondary
            } else {
                gradientOneColor = Secondary500
                gradientTwoColor = GradientSecondary
            }
        }
        Tertiary -> {
            if (isSystemInDarkTheme()) {
                gradientOneColor = Tertiary500
                gradientTwoColor = GradientTertiary
            } else {
                gradientOneColor = Tertiary500
                gradientTwoColor = GradientTertiary
            }
        }
        ComplementaryOne -> {
            if (isSystemInDarkTheme()) {
                gradientOneColor = ComplementaryOne500
                gradientTwoColor = GradientComplementaryOne
            } else {
                gradientOneColor = ComplementaryOne500
                gradientTwoColor = GradientComplementaryOne
            }
        }
        ComplementaryTwo -> {
            if (isSystemInDarkTheme()) {
                gradientOneColor = ComplementaryTwo500
                gradientTwoColor = GradientComplementaryTwo
            } else {
                gradientOneColor = ComplementaryTwo500
                gradientTwoColor = GradientComplementaryTwo
            }
        }
    }


    Box(modifier = modifier.clickable { onClick() }) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(gradientOneColor, gradientOneColor, gradientTwoColor),
                        start = Offset(-startOffset.toFloat(), Float.POSITIVE_INFINITY),
                        end = Offset(Float.POSITIVE_INFINITY, 0f)
                    )
                )
                .blur(0.24.dp)

        ) {
            CustomImage(
                drawableResource = drawable.ic_swipe_indicator, modifier = Modifier
                    .padding(top = 8.dp)
                    .align(Alignment.TopCenter)
            )
            content()
        }
    }
}

sealed class ProductBackGroundType {
    object Primary : ProductBackGroundType()
    object Secondary : ProductBackGroundType()
    object Tertiary : ProductBackGroundType()
    object ComplementaryOne : ProductBackGroundType()
    object ComplementaryTwo : ProductBackGroundType()
}