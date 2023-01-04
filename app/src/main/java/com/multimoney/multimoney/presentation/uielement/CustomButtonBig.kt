package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.Primary400
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.theme.WhiteTransparency10
import com.multimoney.multimoney.presentation.theme.WhiteTransparency40
import com.multimoney.multimoney.presentation.theme.WhiteTransparency70

@Composable
@Preview
fun CustomButtonBig(
    modifier: Modifier = Modifier,
    icon: Int = R.drawable.ic_gear,
    text: String = "Test",
    enabled: Boolean = true,
    onClick: () -> Unit = {}
) {
    val textColor: Color
    val iconTint: Color

    if (isSystemInDarkTheme()) {
        textColor = if (enabled) {
            WhiteTransparency70
        } else {
            WhiteTransparency40
        }
        iconTint = if (enabled) {
            Primary400
        } else {
            WhiteTransparency40
        }
    } else {
        textColor = if (enabled) {
            WhiteTransparency70
        } else {
            WhiteTransparency40
        }
        iconTint = if (enabled) {
            Primary400
        } else {
            WhiteTransparency40
        }
    }

    ConstraintLayout(
        modifier = modifier.clickable {
            if (enabled) {
                onClick.invoke()
            }
        }
    ) {
        val (backgroundRef, iconRef, textRef) = createRefs()
        Box(
            modifier = Modifier
                .constrainAs(backgroundRef) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }
                .clip(RoundedCornerShape(16.dp))
                .background(WhiteTransparency10)
        )
        Icon(
            painter = painterResource(icon),
            contentDescription = "",
            tint = iconTint,
            modifier = Modifier
                .constrainAs(iconRef) {
                    top.linkTo(parent.top, margin = 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }.size(ButtonDefaults.IconSize)
        )
        Text(
            text = text,
            color = textColor,
            style = Typography.caption,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .constrainAs(textRef) {
                    top.linkTo(iconRef.bottom, margin = 8.dp)
                    bottom.linkTo(parent.bottom, margin = 12.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
        )
    }
}
