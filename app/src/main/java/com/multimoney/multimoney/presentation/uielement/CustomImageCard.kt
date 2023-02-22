package com.multimoney.multimoney.presentation.uielement

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter

@Composable
fun CustomImageCard(
    modifier: Modifier = Modifier,
    image: Any?,
    title: String? = null,
    backgroundColor: Color = Color.White,
    isClickable: Boolean = false,
    onClick: () -> Unit = {}
) {
    if (isClickable) {
        modifier.clickable(onClick = onClick)
    }

    Card(
        modifier = modifier,
        backgroundColor = backgroundColor,
        shape = RoundedCornerShape(8.dp),
        elevation = 4.dp
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            when (image) {
                is Drawable -> {
                    /*TODO handle the drawable image*/
                }
                is Bitmap -> {
                    Image(
                        painter = BitmapPainter(image = image.asImageBitmap()),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                else -> {
                    Image(
                        painter = rememberImagePainter(data = image),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
    if (title != null) {
        Text(
            text = title,
            style = MaterialTheme.typography.h6,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}

