package com.multimoney.multimoney.presentation.ui.crypto.purchase

import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposableOpenTarget
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.LocalMultimoneyColors
import com.multimoney.multimoney.presentation.theme.Typography

@Composable
fun ListCryptoCurrencies() {


}

@Composable
fun ListCryptoBody(){
    Text(
        modifier = Modifier.padding(top = 30.dp),
        text = stringResource(id = R.string.crypt_list_purchase_title),
        style = Typography.h6.copy(
            fontWeight = FontWeight.SemiBold,
            color = LocalMultimoneyColors.current.titleText
        ),
        textAlign = TextAlign.Left
    )
}