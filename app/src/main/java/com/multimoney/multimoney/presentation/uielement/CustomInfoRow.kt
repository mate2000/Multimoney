package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.theme.WhiteTransparency50
import com.multimoney.multimoney.presentation.theme.WhiteTransparency60
import com.multimoney.multimoney.presentation.theme.WhiteTransparency90

@Composable
@Preview
fun CustomItemRow(
    modifier: Modifier = Modifier,
    startIcon: Int = R.drawable.ic_payment_points,
    title: String = "",
    subtitle: String = "",
    onClick: () -> Unit = {}
) {
    val titleColor: Color
    val subtitleColor: Color
    val dividerColor: Color

    if (isSystemInDarkTheme()) {
        titleColor = WhiteTransparency90
        subtitleColor = WhiteTransparency60
        dividerColor = WhiteTransparency50
    } else {
        titleColor = WhiteTransparency90
        subtitleColor = WhiteTransparency60
        dividerColor = WhiteTransparency50
    }
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .background(MultimoneyTheme.colors.background)
                .padding(start = 5.dp, top = 24.dp)
                .fillMaxWidth()
                .clickable { onClick() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier.background(MultimoneyTheme.colors.background),
                painter = painterResource(id = startIcon),
                contentDescription = ""
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.fillMaxWidth().padding(end = 16.dp)) {
                Text(
                    text = title,
                    style = Typography.subtitle2.copy(fontWeight = FontWeight.SemiBold),
                    color = titleColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    style = Typography.caption,
                    color = subtitleColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Divider(modifier = Modifier.height(1.dp).fillMaxWidth(), color = dividerColor)
    }
}
