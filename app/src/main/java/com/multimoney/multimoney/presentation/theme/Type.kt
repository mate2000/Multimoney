package com.multimoney.multimoney.presentation.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.multimoney.multimoney.R

val PoppinsFontFamily = FontFamily(
    Font(R.font.poppins_regular)
)

// Set of Material typography styles
val Typography = Typography(
    h1 = TextStyle(
        fontFamily = PoppinsFontFamily,
        fontWeight = FontWeight.W400,
        fontSize = 96.sp,
        letterSpacing = (-1.5).sp,
        lineHeight = 112.sp
    ),
    h2 = TextStyle(
        fontFamily = PoppinsFontFamily,
        fontWeight = FontWeight.W400,
        fontSize = 60.sp,
        letterSpacing = 0.5.sp,
        lineHeight = 72.sp
    ),
    h3 = TextStyle(
        fontFamily = PoppinsFontFamily,
        fontWeight = FontWeight.W400,
        fontSize = 48.sp,
        letterSpacing = 0.sp,
        lineHeight = 56.sp
    ),
    h4 = TextStyle(
        fontFamily = PoppinsFontFamily,
        fontWeight = FontWeight.W400,
        fontSize = 34.sp,
        letterSpacing = 0.25.sp,
        lineHeight = 36.sp
    ),
    h5 = TextStyle(
        fontFamily = PoppinsFontFamily,
        fontWeight = FontWeight.W400,
        fontSize = 24.sp,
        letterSpacing = 0.sp,
        lineHeight = 24.sp
    ),
    h6 = TextStyle(
        fontFamily = PoppinsFontFamily,
        fontWeight = FontWeight.W400,
        fontSize = 20.sp,
        letterSpacing = 0.15.sp,
        lineHeight = 24.sp
    ),
    subtitle1 = TextStyle(
        fontFamily = PoppinsFontFamily,
        fontWeight = FontWeight.W400,
        fontSize = 16.sp,
        letterSpacing = 0.15.sp,
        lineHeight = 24.sp
    ),
    subtitle2 = TextStyle(
        fontFamily = PoppinsFontFamily,
        fontWeight = FontWeight.W400,
        fontSize = 14.sp,
        letterSpacing = 0.1.sp,
        lineHeight = 24.sp
    ),
    body1 = TextStyle(
        fontFamily = PoppinsFontFamily,
        fontWeight = FontWeight.W400,
        fontSize = 16.sp,
        letterSpacing = 0.5.sp,
        lineHeight = 24.sp
    ),
    body2 = TextStyle(
        fontFamily = PoppinsFontFamily,
        fontWeight = FontWeight.W400,
        fontSize = 14.sp,
        letterSpacing = 0.25.sp,
        lineHeight = 20.sp
    ),
    button = TextStyle(
        fontFamily = PoppinsFontFamily,
        fontWeight = FontWeight.W400,
        fontSize = 14.sp,
        letterSpacing = 1.25.sp,
        lineHeight = 26.sp
    ),
    caption = TextStyle(
        fontFamily = PoppinsFontFamily,
        fontWeight = FontWeight.W400,
        fontSize = 12.sp,
        letterSpacing = 0.4.sp,
        lineHeight = 16.sp
    ),
    overline = TextStyle(
        fontFamily = PoppinsFontFamily,
        fontWeight = FontWeight.W400,
        fontSize = 10.sp,
        letterSpacing = 1.5.sp,
        lineHeight = 16.sp
    )
)