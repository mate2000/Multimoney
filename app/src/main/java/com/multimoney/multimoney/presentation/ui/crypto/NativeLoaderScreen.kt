package com.multimoney.multimoney.presentation.ui.crypto

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme

@Preview
@Composable
fun NativeLoaderScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        CircularProgressIndicator(
            modifier = Modifier.wrapContentSize(Alignment.Center),
            color = MultimoneyTheme.colors.circularProgressIndicator
        )
        Text(
            text = stringResource(id = R.string.crypto_buy_processing_transaction),
            color = MultimoneyTheme.colors.labelText,
            fontWeight = FontWeight.Normal,
            fontSize = 22.sp,
            modifier = Modifier.padding(top = 24.dp)
        )
    }
}