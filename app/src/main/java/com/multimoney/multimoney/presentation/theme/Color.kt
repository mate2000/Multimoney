package com.multimoney.multimoney.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Primary
val Primary200 = Color(0xFFDBFFDB)
val Primary300 = Color(0xFF8DEA8D)
val Primary400 = Color(0xFF49D249)
val Primary500 = Color(0xFF21B621)
val Primary600 = Color(0xFF1A851A)
val Primary700 = Color(0xFF015701)
val Primary800 = Color(0xFF003100)

// Secondary
val Secondary200 = Color(0xFFD4FCEB)
val Secondary300 = Color(0xFFA6F2D3)
val Secondary400 = Color(0xFF61E2AD)
val Secondary500 = Color(0xFF24C281)
val Secondary600 = Color(0xFF168F5D)
val Secondary700 = Color(0xFF0E593A)
val Secondary800 = Color(0xFF053A25)

// Semantic Informative
val SemanticInformative200 = Color(0xFFE5F0FF)
val SemanticInformative300 = Color(0xFFB3D1FF)
val SemanticInformative400 = Color(0xFF70A9FF)
val SemanticInformative500 = Color(0xFF0066FF)
val SemanticInformative600 = Color(0xFF0056D6)
val SemanticInformative700 = Color(0xFF003380)
val SemanticInformative800 = Color(0xFF001433)

// Semantic Negative
val SemanticNegative400 = Color (0xFFF37C7C)
val SemanticNegative500 = Color(0xFFE91616)

// Semantic Positive
val SemanticPositive200 = Color(0XFFECFAEA)
val SemanticPositive300 = Color(0XFFC5F0C1)
val SemanticPositive400 = Color(0XFF92E38C)
val SemanticPositive500 = Color(0XFF3CCD32)
val SemanticPositive600 = Color(0XFF32AC2A)
val SemanticPositive700 = Color(0XFF1E6719)
val SemanticPositive800 = Color(0XFF0C290A)

// Complementary 3
val Complementary3500 = Color(0xFFFFBE11)

// GrayScale
val DefaultWhite = Color(0xFFFFFFFF)
val DefaultBlack = Color(0xFF000000)
val GrayScale200 = Color(0xFFf2f2f2)
val GrayScale300 = Color(0xFFD9D9D9)
val GrayScale400 = Color(0xFFB8B8B8)
val GrayScale500 = Color(0xFF8B8B8B)
val GrayScale600 = Color(0xFF5F5F5F)
val GrayScale700 = Color(0xFF393939)
val GrayScale800 = Color(0xFF080808)

// WhiteTransparency
val WhiteTransparency10 = Color(0xFFFFFFFF).copy(alpha = 0.1f)
val WhiteTransparency12 = Color(0xFFFFFFFF).copy(alpha = 0.12f)
val WhiteTransparency20 = Color(0xFFFFFFFF).copy(alpha = 0.2f)
val WhiteTransparency30 = Color(0xFFFFFFFF).copy(alpha = 0.3f)
val WhiteTransparency60 = Color(0xFFFFFFFF).copy(alpha = 0.6f)
val WhiteTransparency70 = Color(0xFFFFFFFF).copy(alpha = 0.7f)
val WhiteTransparency80 = Color(0xFFFFFFFF).copy(alpha = 0.8f)
val WhiteTransparency90 = Color(0xFFFFFFFF).copy(alpha = 0.9f)

// BlackTransparency
val BlackTransparency5 = Color(0xFF000000).copy(alpha = 0.05f)
val BlackTransparency10 = Color(0xFF000000).copy(alpha = 0.1f)
val BlackTransparency12 = Color(0xFF000000).copy(alpha = 0.12f)
val BlackTransparency16 = Color(0xFF000000).copy(alpha = 0.16f)
val BlackTransparency20 = Color(0xFF000000).copy(alpha = 0.2f)
val BlackTransparency30 = Color(0xFF000000).copy(alpha = 0.3f)
val BlackTransparency40 = Color(0xFF000000).copy(alpha = 0.4f)
val BlackTransparency50 = Color(0xFF000000).copy(alpha = 0.5f)
val BlackTransparency60 = Color(0xFF000000).copy(alpha = 0.6f)
val BlackTransparency70 = Color(0xFF000000).copy(alpha = 0.7f)
val BlackTransparency80 = Color(0xFF000000).copy(alpha = 0.8f)
val BlackTransparency90 = Color(0xFF000000).copy(alpha = 0.9f)

@Immutable
data class MultimoneyColors(
    val primary: Color,
    val secondary: Color,
    val background: Color,
    val backgroundSplash: Color,
    val text: Color,
    val onBoardingText: Color,
    val onBoardingSubText: Color,
    val textLink: Color,
    val textInformation: Color,
    val textSubhead: Color,
    val divider: Color,
    val circularProgressIndicator: Color,
    val timerColor: Color
)

val DarkColorPalette = MultimoneyColors(
    primary = Primary700,
    secondary = Secondary500,
    background = GrayScale800,
    backgroundSplash = GrayScale800,
    text = DefaultWhite,
    onBoardingText = WhiteTransparency90,
    onBoardingSubText = WhiteTransparency90,
    textLink = Primary400,
    textInformation = SemanticInformative400,
    textSubhead = GrayScale600,
    divider = GrayScale400,
    circularProgressIndicator = DefaultWhite,
    timerColor = DefaultWhite
)

val LightColorPalette = MultimoneyColors(
    primary = Primary500,
    secondary = Secondary500,
    background = GrayScale800,
    backgroundSplash = GrayScale800,
    text = DefaultWhite,
    onBoardingText = WhiteTransparency90,
    onBoardingSubText = WhiteTransparency90,
    textLink = Primary400,
    textInformation = SemanticInformative400,
    textSubhead = GrayScale600,
    divider = GrayScale400,
    circularProgressIndicator = Primary500,
    timerColor = DefaultBlack
)

val LocalMultimoneyColors = staticCompositionLocalOf {
    LightColorPalette
}

object MultimoneyTheme {
    val colors: MultimoneyColors
        @Composable
        get() = LocalMultimoneyColors.current
}