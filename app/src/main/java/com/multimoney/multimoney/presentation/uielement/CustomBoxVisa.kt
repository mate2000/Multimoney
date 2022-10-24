package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.R.drawable
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.theme.DefaultWhite
import com.multimoney.multimoney.presentation.theme.GradientGrayLiner1
import com.multimoney.multimoney.presentation.theme.GradientGrayLiner2
import com.multimoney.multimoney.presentation.theme.GradientGrey1
import com.multimoney.multimoney.presentation.theme.GradientGrey2
import com.multimoney.multimoney.presentation.theme.Primary400
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.theme.WhiteTransparency10
import com.multimoney.multimoney.presentation.theme.WhiteTransparency40
import com.multimoney.multimoney.presentation.theme.WhiteTransparency70
import com.multimoney.multimoney.presentation.theme.WhiteTransparency90
import com.multimoney.multimoney.presentation.uielement.BoxVisaType.CreditCard
import com.multimoney.multimoney.presentation.uielement.BoxVisaType.RequestCreditCard

@Composable
fun CustomBoxVisaBackground(
    modifier: Modifier = Modifier,
    onClick: (type: BoxVisaType) -> Unit = {},
    shape: Shape = RoundedCornerShape(20.dp),
    isEnable: Boolean = true,
    type: BoxVisaType = RequestCreditCard
) {
    val gradientBorderOneColor: Color
    val gradientBorderTwoColor: Color
    val textColor: Color
    val startIconColor: Color
    val endIconColor: Color

    var innerModifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .clip(shape)

    val buttonColor: ButtonColors = ButtonDefaults.buttonColors(
        backgroundColor = Color.Transparent,
        disabledBackgroundColor = Color.Transparent
    )
    var contentButton: @Composable () -> Unit = {}

    when (type) {
        is RequestCreditCard -> {
            if (isSystemInDarkTheme()) {
                gradientBorderOneColor = GradientGrey1
                gradientBorderTwoColor = GradientGrey2
                if (isEnable) {
                    innerModifier = innerModifier.background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(GradientGrayLiner1, GradientGrayLiner2)
                        )
                    )
                    textColor = WhiteTransparency90
                    startIconColor = DefaultWhite
                    endIconColor = DefaultWhite
                } else {
                    innerModifier = innerModifier.background(WhiteTransparency10)
                    textColor = WhiteTransparency40
                    startIconColor = WhiteTransparency40
                    endIconColor = WhiteTransparency40
                }
            } else {
                gradientBorderOneColor = GradientGrey1
                gradientBorderTwoColor = GradientGrey2
                if (isEnable) {
                    innerModifier = innerModifier.background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(GradientGrayLiner1, GradientGrayLiner2)
                        )
                    )
                    textColor = WhiteTransparency90
                    startIconColor = DefaultWhite
                    endIconColor = DefaultWhite
                } else {
                    innerModifier = innerModifier.background(WhiteTransparency10)
                    textColor = WhiteTransparency40
                    startIconColor = WhiteTransparency40
                    endIconColor = WhiteTransparency40
                }
            }
            contentButton = { RequestCreditCardContent(textColor, endIconColor) }
        }
        is CreditCard -> {
            if (isSystemInDarkTheme()) {
                gradientBorderOneColor = GradientGrey1
                gradientBorderTwoColor = GradientGrey2
                if (isEnable) {
                    innerModifier = innerModifier.background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(GradientGrayLiner1, GradientGrayLiner2)
                        )
                    )
                    textColor = WhiteTransparency70
                    startIconColor = Primary400
                    endIconColor = DefaultWhite
                } else {
                    innerModifier = innerModifier.background(WhiteTransparency10)
                    textColor = WhiteTransparency40
                    startIconColor = WhiteTransparency40
                    endIconColor = WhiteTransparency40
                }
            } else {
                gradientBorderOneColor = GradientGrey1
                gradientBorderTwoColor = GradientGrey2
                if (isEnable) {
                    innerModifier = innerModifier.background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(GradientGrayLiner1, GradientGrayLiner2)
                        )
                    )
                    textColor = WhiteTransparency90
                    startIconColor = Primary400
                    endIconColor = DefaultWhite
                } else {
                    innerModifier = innerModifier.background(WhiteTransparency10)
                    textColor = WhiteTransparency40
                    startIconColor = WhiteTransparency40
                    endIconColor = WhiteTransparency40
                }
            }
            contentButton = {
                CreditCardContent(type.text, textColor, startIconColor, endIconColor)
            }
        }
    }

    innerModifier = innerModifier
        .border(
            width = 1.dp,
            brush = Brush.verticalGradient(
                colors = listOf(gradientBorderOneColor, gradientBorderTwoColor)
            ),
            shape = shape
        )

    Button(
        onClick = { onClick(type) },
        modifier = modifier,
        enabled = isEnable,
        shape = shape,
        colors = buttonColor,
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(modifier = innerModifier) {
            contentButton()
        }
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun CreditCardContent(
    cardNumber: String,
    textColor: Color,
    startIconColor: Color,
    endIconColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = drawable.ic_nfc),
            contentDescription = "",
            tint = startIconColor
        )
        Text(
            text = cardNumber,
            modifier = Modifier.padding(start = 16.dp, top = 18.dp, bottom = 18.dp),
            style = Typography.body2.copy(
                fontWeight = FontWeight.SemiBold,
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false
                )
            ),
            color = textColor
        )
        Icon(
            painter = painterResource(id = drawable.ic_visa_logo),
            contentDescription = "",
            modifier = Modifier
                .height(16.dp)
                .padding(start = 12.dp),
            tint = endIconColor
        )
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun RequestCreditCardContent(textColor: Color, endIconColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = string.home_active_credit_card_label),
            modifier = Modifier.padding(vertical = 18.dp),
            style = Typography.body2.copy(
                fontWeight = FontWeight.SemiBold,
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false
                )
            ),
            color = textColor
        )
        Icon(
            painter = painterResource(id = drawable.ic_visa_logo),
            contentDescription = "",
            modifier = Modifier
                .height(14.dp)
                .padding(start = 8.dp),
            tint = endIconColor
        )
    }
}

sealed class BoxVisaType {
    data class CreditCard(val text: String) : BoxVisaType()
    object RequestCreditCard : BoxVisaType()
}
