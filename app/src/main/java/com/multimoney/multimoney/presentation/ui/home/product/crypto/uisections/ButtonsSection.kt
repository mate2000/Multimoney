package com.multimoney.multimoney.presentation.ui.home.product.crypto.uisections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.GrayScale700
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType

@Composable
fun ButtonsSection(
    actionWallet: () -> Unit,
    actionMarket: () -> Unit,
    walletEnable: Boolean = false
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CustomButton(
            modifier = Modifier
                .padding(8.dp)
                .width(164.dp)
                .wrapContentHeight(),
            buttonType = CustomButtonType.PrimaryQuaternary,
            text = stringResource(R.string.crypto_footer_expanded_btn_wallet),
            enable = walletEnable,
            onClick = actionWallet
        )
        CustomButton(
            modifier = Modifier
                .padding(8.dp)
                .width(164.dp)
                .wrapContentHeight(),
            buttonType = CustomButtonType.PrimaryQuaternary,
            text = stringResource(R.string.crypto_footer_expanded_btn_market),
            onClick = actionMarket
        )
    }
}