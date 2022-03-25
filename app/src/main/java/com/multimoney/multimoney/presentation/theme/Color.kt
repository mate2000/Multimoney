package com.multimoney.multimoney.presentation.theme

import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color

// Primary
val Primary200 = Color(0xFFDBE7FF)
val Primary400 = Color(0xFF538EFF)
val Primary500 = Color(0xFF0F62FF)
val Primary600 = Color(0xFF0046CA)
val Primary700 = Color(0xFF002E86)

// Secondary
val Secondary500 = Color(0xFF5E5BDB)

// Semantic Negative
val SemanticNegative500 = Color(0xFFE91616)

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
val WhiteTransparency70 = Color(0xFFFFFFFF).copy(alpha = 0.7f)

// Dark Theme
val Purple500 = Color(0xFF6200EE)
val Purple700 = Color(0xFF3700B3)

val DarkColorPalette = darkColors(
    primary = Primary500,
    secondary = Secondary500,
    background = DefaultWhite
)

val LightColorPalette = lightColors(
    primary = Purple700,
    secondary = Purple500,
    background = DefaultBlack
)