package com.multimoney.multimoney.presentation.ui.home.product.crypto

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography

@Composable
fun CryptoFooter() {
    //content
    Text(
        text = "Crypto Footer",
        style = Typography.h6.copy(letterSpacing = 0.38.sp),
        color = MultimoneyTheme.colors.labelText
    )
}