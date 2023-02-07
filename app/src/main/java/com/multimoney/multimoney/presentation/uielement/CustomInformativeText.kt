package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * CustomInformativeText: Informative text to match design style across the whole app, in order to use it.
 *
 * Parameters:
 * @param modifier: Apply style.
 * @param leadingIcon: Drawable resource at start.
 * @param trailingIcon: Drawable resource at the end.
 * @param leadingIconClick: Drawable resource at start click action.
 * @param trailingIconClick: Drawable resource at the end click action.
 * @param text: Text to display information.
 * @param textStyle: Apply text style.
 */

@Composable
@Preview
fun CustomInformativeText(
    modifier: Modifier = Modifier,
    leadingIcon: Int? = null,
    trailingIcon: Int? = null,
    leadingIconClick: () -> Unit = {},
    trailingIconClick: () -> Unit = {},
    text: String = "",
    textStyle: TextStyle = TextStyle(),
    alignmentVertical: Alignment.Vertical = CenterVertically,
    iconSize: Dp = 16.dp
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.SpaceBetween) {
        leadingIcon?.let {
            Image(
                modifier = Modifier
                    .align(alignmentVertical)
                    .clickable { leadingIconClick() }
                    .size(iconSize),
                painter = painterResource(it),
                contentDescription = "",
                contentScale = ContentScale.Fit
            )
        }
        Text(
            text = text,
            style = textStyle,
            modifier = Modifier
                .wrapContentSize()
                .padding(start = 9.dp)
                .align(alignmentVertical)
        )
        trailingIcon?.let {
            Image(
                modifier = Modifier
                    .align(alignmentVertical)
                    .clickable { trailingIconClick() }
                    .size(iconSize),
                painter = painterResource(it),
                contentDescription = "",
                contentScale = ContentScale.Fit
            )
        }
    }
}
