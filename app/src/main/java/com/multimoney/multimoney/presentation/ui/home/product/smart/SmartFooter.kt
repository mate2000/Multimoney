package com.multimoney.multimoney.presentation.ui.home.product.smart

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography

@Composable
fun SmartFooter() {
    // TODO, add smart footer when available, passing an empty text for now
    //  due to this ticket: https://akros.atlassian.net/browse/REV-2804
    Text(
        text = "",
        style = Typography.h6.copy(letterSpacing = 0.38.sp),
        color = MultimoneyTheme.colors.labelText
    )
}
