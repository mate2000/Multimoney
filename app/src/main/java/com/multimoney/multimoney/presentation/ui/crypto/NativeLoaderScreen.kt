package com.multimoney.multimoney.presentation.ui.crypto

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator

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
            Box(
                modifier = Modifier
                    .constrainAs(mmLogo) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                        start.linkTo(parent.start)
                        bottom.linkTo(parent.bottom)
                    }
            ) {
                LoadingIndicator(
                )
            }
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