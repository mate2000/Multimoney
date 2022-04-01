package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme

/**
 * LoadingIndicator: Circular progress indicator used to indicate a background task is running
 *
 * Parameters:
 * @param isLoading: Display loading indicator.
 */

@Composable
@Preview
fun LoadingIndicator(
    isLoading: Boolean = true
) {
    if (isLoading) {
        Surface(modifier = Modifier.fillMaxSize(), color = Color.Transparent) {
            CircularProgressIndicator(
                modifier = Modifier.wrapContentSize(Alignment.Center),
                color = MultimoneyTheme.colors.circularProgressIndicator
            )
        }
    }
}
