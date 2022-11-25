package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.solver.widgets.Optimizer
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.theme.WhiteTransparency90

/**
 * CustomThreePointsTextButton: This component is used to create text buttons*
 * Parameters:
 * @param modifier: Apply style.
 * @param textResource: Text to convert into button.
 * @param startIconResource: End Icon to complement button.
 * @param onClick: This will happen when user perform click on text button.
 */

@Composable
@Preview
fun CustomThreePointsTextButton(
    modifier: Modifier = Modifier,
    textResource: Int = R.string.empty,
    startIconResource: Int = 0,
    onClick: () -> Unit = {}
) {
    val textColor = if (isSystemInDarkTheme()) {
        WhiteTransparency90
    } else {
        WhiteTransparency90
    }
    Box(modifier = modifier) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onClick()
                },
            optimizationLevel = Optimizer.OPTIMIZATION_DIRECT
        ) {
            val (icon, title) = createRefs()
            Image(
                modifier = Modifier.constrainAs(icon) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                },
                painter = painterResource(id = startIconResource),
                contentDescription = ""
            )
            Text(
                text = stringResource(id = textResource),
                modifier = Modifier.wrapContentSize().padding(start = 24.dp).constrainAs(title) {
                    start.linkTo(icon.end)
                    top.linkTo(icon.top)
                    bottom.linkTo(icon.bottom)
                },
                style = Typography.button,
                color = textColor
            )
        }
    }
}
