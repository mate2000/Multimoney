package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.Primary500

/**
 * BackCloseNavBar: NavBar with options to go back and close current screen
 *
 * Parameters:
 * @param isLeftButtonVisible: Enable back button.
 * @param isRightButtonVisible: Enable close button.
 * @param onLeftButtonClick: Action when user clicks back button.
 * @param onRightButtonClick: Action when user clicks close button.
 */

@Composable
@Preview
fun TopNavBar(
    isLeftButtonVisible: Boolean = true,
    isRightButtonVisible: Boolean = true,
    isCenterContentVisible: Boolean = false,
    leftButtonIcon: Int = R.drawable.ic_nav_icon_left,
    rightButtonIcon: Int = R.drawable.ic_nav_icon_right,
    centerIcon: Int = R.drawable.ic_logo_multimoney2,
    onLeftButtonClick: () -> Unit = {},
    onRightButtonClick: () -> Unit = {}
) {
    val tint = if (isSystemInDarkTheme()) {
        Primary500
    } else {
        Primary500
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        val (leftIconId, centerContentId, rightIconId) = createRefs()
        Column(
            modifier = Modifier.constrainAs(leftIconId) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
            }
        ) {
            if (isLeftButtonVisible) {
                IconButton(onClick = { onLeftButtonClick() }) {
                    Icon(
                        painter = painterResource(leftButtonIcon),
                        contentDescription = "",
                        tint = tint
                    )
                }
            }
        }
        Box(
            modifier = Modifier.constrainAs(centerContentId) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            },
            contentAlignment = Alignment.Center
        ) {
            if (isCenterContentVisible) {
                Image(painter = painterResource(id = centerIcon), contentDescription = "")
            }
        }
        Column(
            modifier = Modifier.constrainAs(rightIconId) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                end.linkTo(parent.end)
            }
        ) {
            if (isRightButtonVisible) {
                IconButton(onClick = { onRightButtonClick() }) {
                    Icon(
                        painter = painterResource(rightButtonIcon),
                        contentDescription = "",
                        tint = tint
                    )
                }
            }
        }
    }
}
