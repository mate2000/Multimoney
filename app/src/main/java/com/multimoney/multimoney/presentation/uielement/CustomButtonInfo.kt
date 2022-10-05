package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.GradientGrey1
import com.multimoney.multimoney.presentation.theme.GradientGrey2
import com.multimoney.multimoney.presentation.theme.WhiteTransparency5

@Composable
fun CustomButtonInfo(
    startIcon: Int = 0,
    title: Int = R.string.empty,
    subtitle: Int = R.string.empty,
    endIcon: Int = R.drawable.ic_right_chevron,
    onClick: () -> Unit = {}
) {

    val gradientBorderOneColor: Color
    val gradientBorderTwoColor: Color
    val background: Color

    if (isSystemInDarkTheme()) {
        gradientBorderOneColor = GradientGrey1
        gradientBorderTwoColor = GradientGrey2
        background = WhiteTransparency5
    } else {
        gradientBorderOneColor = GradientGrey1
        gradientBorderTwoColor = GradientGrey2
        background = WhiteTransparency5
    }

    Button(
        onClick = onClick, modifier = Modifier
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(gradientBorderOneColor, gradientBorderTwoColor)
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .background(background)
    ) {
        ConstraintLayout {
            val (startIconId, titleId, subTitleId, endIconId) = createRefs()

            if (startIcon != 0) {
                Image(
                    painter = painterResource(id = startIcon),
                    contentDescription = "",
                    modifier = Modifier.constrainAs(startIconId) {

                    })
            }

            Text(text = stringResource(id = title), modifier = Modifier.constrainAs(titleId) {

            })
            Text(text = stringResource(id = subtitle), modifier = Modifier.constrainAs(subTitleId) {

            })

            Image(painter = painterResource(id = endIcon), contentDescription = "")
        }
    }
}