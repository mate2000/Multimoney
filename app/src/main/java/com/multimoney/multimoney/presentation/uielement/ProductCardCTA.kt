package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.DefaultWhite
import com.multimoney.multimoney.presentation.theme.Typography

@Composable
@Preview
fun ProductCardCTA(
    modifier: Modifier = Modifier,
    actionText: String? = ""
) {
    val notDefinedValue = stringResource(id = R.string.not_defined)
    val text: String? = if (actionText != notDefinedValue) actionText else null
    val textColor = if (isSystemInDarkTheme()) DefaultWhite else DefaultWhite
    Column(modifier = modifier.fillMaxWidth()) {
        text?.let {
            if (text.isNotBlank()) {
                CustomImage(
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .align(Alignment.CenterHorizontally),
                    drawableResource = R.drawable.ic_chevron_up
                )

                Text(
                    text = text,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    style = Typography.body2.copy(fontWeight = FontWeight.SemiBold),
                    color = textColor,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}