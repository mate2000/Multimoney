package com.multimoney.multimoney.presentation.ui.home.product.credit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.uielement.TopNavBar

@Composable
fun CreditHeaderExpanded(backPressed: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.background)
    ) {
        TopNavBar(
            isRightButtonVisible = false,
            onLeftButtonClick = {
                backPressed()
            }
        )
        Text(
            text = stringResource(id = R.string.home_product_credit_header_expanded_title),
            modifier = Modifier.padding(horizontal = 16.dp),
            style = Typography.h5.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.labelText
        )
    }
}
