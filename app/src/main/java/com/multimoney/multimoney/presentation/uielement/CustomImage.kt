package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource

/**
 * CustomImage: Image to match design style across the whole app, in order to use it.
 *
 * Parameters:
 * @param modifier: Apply style.
 * @param contentDescription: Content description.
 * @param drawableResource: Drawable resource.
 */

@Composable
fun CustomImage(
    modifier: Modifier = Modifier,
    contentDescription: String = "",
    drawableResource: Int
) = Image(
    painter = painterResource(drawableResource),
    contentDescription = contentDescription,
    modifier = modifier
)
