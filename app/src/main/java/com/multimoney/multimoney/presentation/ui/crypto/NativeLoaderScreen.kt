package com.multimoney.multimoney.presentation.ui.crypto

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme

@Preview
@Composable
fun NativeLoaderScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.background)
    ) {
        ConstraintLayout(
            modifier = Modifier.fillMaxSize()
        ) {
            val (mmLogo, infoText) = createRefs()

            Icon(
                painter = painterResource(R.drawable.ic_multimoney_white_logo),
                contentDescription = "",
                tint = Color.White,
                modifier = Modifier.constrainAs(mmLogo){
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end )
                }
            )
            Text(
                text = stringResource(id = R.string.crypto_buy_processing_transaction),
                modifier = Modifier
                    .constrainAs(infoText) {
                        top.linkTo(mmLogo.bottom)
                        end.linkTo(parent.end)
                        start.linkTo(parent.start)
                    }
                    .padding(top = 24.dp),
                color = MultimoneyTheme.colors.labelText,
                fontWeight = FontWeight.Normal,
                fontSize = 22.sp
            )
        }
    }
}