package com.multimoney.multimoney.presentation.uielement

import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography

@Composable
fun LoadingMultiMoney(
    @StringRes
    textRes: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CustomImage(
            drawableResource = R.drawable.ic_logo_multimoney,
            modifier = Modifier
                .wrapContentSize()
                .size(48.dp, 48.dp)
        )
        Text(
            text = stringResource(id = textRes),
            modifier = Modifier.padding(top = 32.dp),
            style = Typography.h5.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.text,
            textAlign = TextAlign.Center
        )
    }
}