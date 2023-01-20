package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.theme.WhiteTransparency5
import com.multimoney.multimoney.presentation.theme.WhiteTransparency60
import com.multimoney.multimoney.presentation.theme.WhiteTransparency90
import com.multimoney.multimoney.presentation.util.toTwoChar

/**
 * CustomButton: Button to match design style across the whole app, in order to use it.
 *
 * Parameters:
 * @param modifier: Apply style.
 * @param title: titular title.
 * @param subtitle: phone number.
 * @param colorSubtitle: icon color.
 * @param endIcon: three dots icon.
 * @param shouldCenterEndIcon: center the icon.
 * @param onEndIconClick: action that the icon will have.
 */

@Composable
fun ContactItem(
    modifier: Modifier = Modifier,
    title: String? = "",
    subtitle: String = "",
    colorSubtitle: Color,
    endIcon: Int? = R.drawable.ic_options,
    shouldCenterEndIcon: Boolean = true,
    onEndIconClick: () -> Unit = {}
) {
    val background: Color
    val titleColor: Color
    val subtitleColor: Color

    if (isSystemInDarkTheme()) {
        background = Color.Transparent
        titleColor = WhiteTransparency90
        subtitleColor = WhiteTransparency60
    } else {
        background = WhiteTransparency5
        titleColor = WhiteTransparency90
        subtitleColor = WhiteTransparency60
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.background(background)
    ) {
        ConstraintLayout(
            modifier = modifier
                .background(background)
        ) {
            val (startIconId, titleId, subTitleId, endIconId) = createRefs()
            Surface(
                shape = CircleShape,
                modifier = Modifier
                    .size(60.dp)
                    .constrainAs(startIconId) {
                        top.linkTo(parent.top, margin = 17.dp)
                        start.linkTo(parent.start, margin = 4.dp)
                        bottom.linkTo(parent.bottom, margin = 17.dp)
                    },
                border = BorderStroke(1.dp, Color.Gray),
                color = background
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = title.toTwoChar(),
                        style = Typography.h6.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = colorSubtitle
                    )
                }
            }
            if (title != null) {
                Text(
                    text = title,
                    modifier = Modifier.constrainAs(titleId) {
                        top.linkTo(startIconId.top, margin = 8.dp)
                        start.linkTo(startIconId.end, margin = 22.dp)
                        if (endIcon != null) {
                            end.linkTo(endIconId.start, margin = 16.dp)
                        } else {
                            end.linkTo(parent.end, margin = 16.dp)
                        }
                        bottom.linkTo(subTitleId.top)
                        height = Dimension.fillToConstraints
                        width = Dimension.fillToConstraints
                    },
                    style = Typography.body2.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    ),
                    color = titleColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                modifier = Modifier.constrainAs(subTitleId) {
                    top.linkTo(titleId.bottom, margin = 8.dp)
                    start.linkTo(titleId.start)
                    height = Dimension.fillToConstraints
                },
                text = subtitle,
                style = Typography.caption.copy(fontSize = 13.sp),
                color = subtitleColor
            )
            if (endIcon != null) {
                Image(
                    painter = painterResource(id = endIcon),
                    modifier = Modifier
                        .constrainAs(endIconId) {
                            if (shouldCenterEndIcon) {
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                            } else {
                                // align the icon to the top
                                top.linkTo(parent.top, 17.dp)
                            }
                            end.linkTo(parent.end, margin = 18.dp)
                        }
                        .clickable {
                            onEndIconClick()
                        },
                    contentDescription = ""
                )
            }

        }
    }
}