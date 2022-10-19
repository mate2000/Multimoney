package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.GradientGrey1
import com.multimoney.multimoney.presentation.theme.GradientGrey2
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.theme.WhiteTransparency5
import com.multimoney.multimoney.presentation.theme.WhiteTransparency60
import com.multimoney.multimoney.presentation.theme.WhiteTransparency90

@Composable
@Preview
fun CustomInfoButton(
    modifier: Modifier = Modifier,
    startIcon: Int = R.drawable.ic_payment_fee_icon,
    title: String = "",
    subtitle: String = "",
    endIcon: Int = R.drawable.ic_right_chevron,
    onClick: () -> Unit = {},
    endIconClick: () -> Unit = {}
) {
    val buttonColor: ButtonColors = ButtonDefaults.buttonColors(
        backgroundColor = Color.Transparent,
        disabledBackgroundColor = Color.Transparent
    )

    val gradientBorderOneColor: Color
    val gradientBorderTwoColor: Color
    val background: Color
    val titleColor: Color
    val subtitleColor: Color

    if (isSystemInDarkTheme()) {
        gradientBorderOneColor = GradientGrey1
        gradientBorderTwoColor = GradientGrey2
        background = WhiteTransparency5
        titleColor = WhiteTransparency90
        subtitleColor = WhiteTransparency60
    } else {
        gradientBorderOneColor = GradientGrey1
        gradientBorderTwoColor = GradientGrey2
        background = WhiteTransparency5
        titleColor = WhiteTransparency90
        subtitleColor = WhiteTransparency60
    }

    Button(
        onClick = onClick,
        modifier = modifier
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(gradientBorderOneColor, gradientBorderTwoColor)
                ),
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = buttonColor,
        contentPadding = PaddingValues(0.dp)
    ) {
        ConstraintLayout(Modifier.background(background).fillMaxWidth()) {
            val (startIconId, titleId, subTitleId, endIconId) = createRefs()
            Image(
                painter = painterResource(id = startIcon),
                contentDescription = "",
                modifier = Modifier.constrainAs(startIconId) {
                    top.linkTo(parent.top, margin = 12.dp)
                    start.linkTo(parent.start, margin = 12.dp)
                    bottom.linkTo(parent.bottom, margin = 12.dp)
                }
            )
            Text(
                text = title,
                modifier = Modifier.constrainAs(titleId) {
                    top.linkTo(startIconId.top, margin = 4.dp)
                    start.linkTo(startIconId.end, margin = 16.dp)
                },
                style = Typography.body2.copy(fontWeight = FontWeight.SemiBold),
                color = titleColor
            )
            Text(
                text = subtitle,
                modifier = Modifier.constrainAs(subTitleId) {
                    top.linkTo(titleId.bottom, margin = 4.dp)
                    start.linkTo(titleId.start)
                },
                style = Typography.caption,
                color = subtitleColor
            )
            Image(
                painter = painterResource(id = endIcon),
                modifier = Modifier.constrainAs(endIconId) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end, margin = 12.dp)
                    bottom.linkTo(parent.bottom)
                }.clickable {
                    endIconClick()
                },
                contentDescription = ""
            )
        }
    }
}
