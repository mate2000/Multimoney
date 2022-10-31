package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

/**
 * CustomInformativeText: Informative text to match design style across the whole app, in order to use it.
 *
 * Parameters:
 * @param modifier: Apply style.
 * @param drawableResource: Drawable resource.
 * @param text: Text to display information.
 * @param textStyle: Apply text style.
 */

@Composable
fun CustomInformativeText(
    modifier: Modifier = Modifier,
    drawableResource: Int = 0,
    text: String,
    textStyle: TextStyle
) {
    Row(modifier = modifier) {
        Image(
            modifier = Modifier.align(Alignment.CenterVertically),
            painter = painterResource(drawableResource),
            contentDescription = "",
            contentScale = ContentScale.Fit
        )
        Text(
            text = text,
            style = textStyle,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 9.dp)
                .align(Alignment.CenterVertically)
        )
    }
}
