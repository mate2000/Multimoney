package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.Primary500

/**
 * BackCloseNavBar: NavBar with options to go back and close current screen
 *
 * Parameters:
 * @param isBackVisible: Enable back button.
 * @param isCloseVisible: Enable close button.
 * @param onBackClick: Action when user clicks back button.
 * @param onCloseClick: Action when user clicks close button.
 */

@Composable
@Preview
fun BackCloseNavBar(
    isBackVisible: Boolean = true,
    isCloseVisible: Boolean = true,
    onBackClick: () -> Unit = {},
    onCloseClick: () -> Unit = {}
) {

    val tint = if (isSystemInDarkTheme()) {
        Primary500
    } else {
        Primary500
    }

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(Modifier.weight(0.9f)) {
            if (isBackVisible) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_left),
                        contentDescription = "",
                        tint = tint
                    )
                }
            }
        }
        Column(Modifier.weight(0.1f)) {
            if (isCloseVisible) {
                IconButton(onClick = onCloseClick) {
                    Icon(
                        painter = painterResource(R.drawable.ic_close),
                        contentDescription = "",
                        tint = tint
                    )
                }
            }
        }
    }
}
