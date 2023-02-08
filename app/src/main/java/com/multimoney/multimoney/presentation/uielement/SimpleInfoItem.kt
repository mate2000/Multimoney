package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.theme.WhiteTransparency90

/**
 * a simple composable item to show an icon and a text together horizontally
 */
@Composable
@Preview
fun SimpleItemRow(
    modifier: Modifier = Modifier,
    startIcon: Int = R.drawable.ic_edit_bg,
    title: String = "",
    titleFontWeight: FontWeight = FontWeight.SemiBold,
    shouldShowDivider: Boolean = true,
    onClick: () -> Unit = {},
) {
    val titleColor: Color
    val backgroundColor: Color

    if (isSystemInDarkTheme()) {
        titleColor = WhiteTransparency90
        backgroundColor = Transparent
    } else {
        titleColor = WhiteTransparency90
        backgroundColor = Transparent
    }
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .background(backgroundColor)
                .padding(start = 0.dp, top = 16.dp, bottom = 16.dp)
                .fillMaxWidth()
                .clickable { onClick() },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(id = startIcon),
                contentDescription = "",
            )
            Spacer(modifier = Modifier.width(24.dp))
            Column {
                Text(
                    text = title,
                    style = Typography.subtitle2.copy(fontWeight = titleFontWeight),
                    color = titleColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
