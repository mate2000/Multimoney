package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.Dimension.Companion
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
    imageModifier: Modifier = Modifier,
    startIcon: Int? = R.drawable.ic_payment_fee_icon,
    title: String = "",
    subtitle: String = "",
    subtitle2: String = "",
    endIcon: Int? = R.drawable.ic_right_chevron,
    shouldCenterEndIcon: Boolean = true,
    onClick: () -> Unit = {},
    onEndIconClick: () -> Unit = {},
    enable: Boolean = true
) {
    val buttonColor: ButtonColors = ButtonDefaults.buttonColors(
        backgroundColor = Transparent,
        disabledBackgroundColor = Transparent
    )

    val gradientBorderOneColor: Color
    val gradientBorderTwoColor: Color
    val background: Color
    val titleColor: Color
    val subtitleColor: Color

    if (isSystemInDarkTheme()) {
        gradientBorderOneColor = GradientGrey1
        gradientBorderTwoColor = GradientGrey2
        background = if (enable) WhiteTransparency5 else Transparent
        titleColor = WhiteTransparency90
        subtitleColor = WhiteTransparency60
    } else {
        gradientBorderOneColor = GradientGrey1
        gradientBorderTwoColor = GradientGrey2
        background = if (enable) WhiteTransparency5 else Transparent
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
            ).wrapContentHeight(),
        shape = RoundedCornerShape(20.dp),
        colors = buttonColor,
        contentPadding = PaddingValues(0.dp),
        enabled = enable
    ) {
        ConstraintLayout(Modifier.background(background).fillMaxWidth()) {
            val (startIconId, titleId, subTitleId, subTitle2Id, endIconId) = createRefs()
            if (startIcon != null) {
                Image(
                    painter = painterResource(id = startIcon),
                    contentDescription = "",
                    modifier = imageModifier.constrainAs(startIconId) {
                        top.linkTo(parent.top, margin = 17.dp)
                        start.linkTo(parent.start, margin = 18.dp)
                        bottom.linkTo(parent.bottom, margin = 17.dp)
                    }
                )
            }
            if (subtitle.isNotEmpty()) {
                Text(
                    text = title,
                    modifier = Modifier.constrainAs(titleId) {
                        if (startIcon != null) {
                            top.linkTo(startIconId.top)
                            start.linkTo(startIconId.end, margin = 22.dp)
                        } else {
                            top.linkTo(parent.top, margin = 16.dp)
                            start.linkTo(parent.start, margin = 16.dp)
                        }
                        if (endIcon != null) {
                            end.linkTo(endIconId.start, margin = 16.dp)
                        } else {
                            end.linkTo(parent.end, margin = 16.dp)
                        }
                        bottom.linkTo(subTitleId.top)
                        height = Dimension.fillToConstraints
                        width = Dimension.fillToConstraints
                    },
                    style = Typography.body2.copy(fontWeight = FontWeight.SemiBold),
                    color = titleColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    modifier = Modifier.constrainAs(subTitleId) {
                        top.linkTo(titleId.bottom, margin = 4.dp)
                        start.linkTo(titleId.start)
                        bottom.linkTo(parent.bottom, margin = 10.dp)
                        end.linkTo(endIconId.start, margin = 10.dp)
                        width = Dimension.fillToConstraints
                        height = Dimension.wrapContent
                    },
                    style = Typography.caption,
                    color = subtitleColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (subtitle2.isNotEmpty()) {
                    Text(
                        text = subtitle2,
                        modifier = Modifier.constrainAs(subTitle2Id) {
                            top.linkTo(subTitleId.bottom, margin = 4.dp)
                            start.linkTo(subTitleId.start)
                            bottom.linkTo(parent.bottom, margin = 16.dp)
                        },
                        style = Typography.caption,
                        color = subtitleColor
                    )
                }
            } else {
                Text(
                    text = title,
                    modifier = Modifier.constrainAs(titleId) {
                        if (startIcon != null) {
                            top.linkTo(startIconId.top, margin = 4.dp)
                            start.linkTo(startIconId.end, margin = 16.dp)
                        } else {
                            top.linkTo(parent.top, margin = 4.dp)
                            start.linkTo(parent.start, margin = 16.dp)
                        }
                        if (endIcon != null) {
                            end.linkTo(endIconId.start, margin = 16.dp)
                        } else {
                            end.linkTo(parent.end, margin = 16.dp)
                        }
                        bottom.linkTo(startIconId.bottom)
                        width = Dimension.fillToConstraints
                    },
                    style = Typography.body2.copy(fontWeight = FontWeight.SemiBold),
                    color = titleColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (endIcon != null) {
                Image(
                    painter = painterResource(id = endIcon),
                    modifier = Modifier.constrainAs(endIconId) {
                        if (shouldCenterEndIcon) {
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        } else {
                            // align the icon to the top
                            top.linkTo(parent.top, 17.dp)
                        }
                        end.linkTo(parent.end, margin = 12.dp)
                    }.clickable {
                        onEndIconClick()
                    },
                    contentDescription = ""
                )
            }
        }
    }
}
