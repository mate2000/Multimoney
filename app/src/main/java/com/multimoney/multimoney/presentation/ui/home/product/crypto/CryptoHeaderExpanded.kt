package com.multimoney.multimoney.presentation.ui.home.product.crypto

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
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
fun CryptoHeaderExpanded(backPressed: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        TopNavBar(
            isRightButtonVisible = false,
            onLeftButtonClick = {
                backPressed()
            }
        )
        Text(
            text = stringResource(R.string.home_crypto_header_expanded_title),
            modifier = Modifier.padding(horizontal = 16.dp),
            style = Typography.h5.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.labelText
        )
    }
}