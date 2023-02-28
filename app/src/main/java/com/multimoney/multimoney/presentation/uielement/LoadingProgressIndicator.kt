package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography


@Composable
fun loadingProgressIndicator(isLoading: Boolean = true, text: String = "") {
    if (isLoading) {
        Row(
            modifier = Modifier
                .padding(bottom = 22.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(color = Color.Transparent) {
                CircularProgressIndicator(
                    modifier = Modifier.wrapContentSize(Alignment.CenterStart),
                    color = MultimoneyTheme.colors.circularProgressIndicator
                )
                Spacer(Modifier.weight(10f))
                Text(
                    text = text,
                    modifier = Modifier.padding(start = 30.dp),
                    style = Typography.body1,
                    color = MultimoneyTheme.colors.text,
                    textAlign = TextAlign.End
                )
            }
        }

    }
}