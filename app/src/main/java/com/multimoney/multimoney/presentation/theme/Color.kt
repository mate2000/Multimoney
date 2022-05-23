package com.multimoney.multimoney.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Primary
val Primary200 = Color(0xFFDBE7FF)
val Primary300 = Color(0xFF97BBFF)
val Primary400 = Color(0xFF538EFF)
val Primary500 = Color(0xFF0F62FF)
val Primary600 = Color(0xFF0046CA)
val Primary700 = Color(0xFF002E86)

// Secondary
val Secondary500 = Color(0xFF4C49CD)

// Semantic Negative
val SemanticNegative500 = Color(0xFFE91616)

// Semantic Positive
val SemanticPositive600 = Color(0XFF32AC2A)

// Complementary 3
val Complementary3500 = Color(0xFFFFBE11)

// GrayScale
val DefaultWhite = Color(0xFFFFFFFF)
val DefaultBlack = Color(0xFF000000)
val GrayScale200 = Color(0xFFF2F2F2)
val GrayScale300 = Color(0xFFD9D9D9)
val GrayScale400 = Color(0xFFB8B8B8)
val GrayScale500 = Color(0xFF808080)
val GrayScale600 = Color(0xFF6B6B6B)
val GrayScale700 = Color(0xFF404040)
val GrayScale800 = Color(0xFF1A1A1A)

// WhiteTransparency
val WhiteTransparency10 = Color(0xFFFFFFFF).copy(alpha = 0.1f)
val WhiteTransparency12 = Color(0xFFFFFFFF).copy(alpha = 0.12f)
val WhiteTransparency30 = Color(0xFFFFFFFF).copy(alpha = 0.3f)
val WhiteTransparency60 = Color(0xFFFFFFFF).copy(alpha = 0.6f)
val WhiteTransparency70 = Color(0xFFFFFFFF).copy(alpha = 0.7f)
val WhiteTransparency80 = Color(0xFFFFFFFF).copy(alpha = 0.8f)

// BlackTransparency
val BlackTransparency50 = Color(0xFF000000).copy(alpha = 0.5f)

// Dark Theme
val Purple500 = Color(0xFF6200EE)
val Purple700 = Color(0xFF3700B3)

@Immutable
data class MultimoneyColors(
    val primary: Color,
    val secondary: Color,
    val background: Color,
    val backgroundSplash: Color,
    val text: Color,
    val textLink: Color,
    val textInformation: Color,
    val textSubhead: Color,
    val divider: Color,
    val circularProgressIndicator: Color
)

val DarkColorPalette = MultimoneyColors(
    primary = Purple700,
    secondary = Purple500,
    background = DefaultBlack,
    backgroundSplash = Primary200,
    text = DefaultWhite,
    textLink = Primary500,
    textInformation = Primary500,
    textSubhead = GrayScale600,
    divider = GrayScale400,
    circularProgressIndicator = DefaultWhite
)

val LightColorPalette = MultimoneyColors(
    primary = Primary500,
    secondary = Secondary500,
    background = DefaultWhite,
    backgroundSplash = Primary200,
    text = GrayScale800,
    textLink = Primary500,
    textInformation = Primary500,
    textSubhead = GrayScale600,
    divider = GrayScale400,
    circularProgressIndicator = Primary500
)

val LocalMultimoneyColors = staticCompositionLocalOf {
    LightColorPalette
}

object MultimoneyTheme {
    val colors: MultimoneyColors
        @Composable
        get() = LocalMultimoneyColors.current
}