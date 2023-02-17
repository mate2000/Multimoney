package com.multimoney.multimoney.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun MultimoneyTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val systemUIController = rememberSystemUiController()

    val colors = if (darkTheme) {
        systemUIController.setStatusBarColor(GrayScale800, false)
        DarkColorPalette
    } else {
        systemUIController.setStatusBarColor(GrayScale800, true)
        LightColorPalette
    }
    CompositionLocalProvider(LocalMultimoneyColors provides colors) {
        MaterialTheme(
            typography = Typography,
            shapes = Shapes,
            content = content,
        )
    }
}
