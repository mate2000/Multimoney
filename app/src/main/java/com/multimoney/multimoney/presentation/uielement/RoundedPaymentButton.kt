package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.presentation.theme.ComplementaryBlack
import com.multimoney.multimoney.presentation.theme.ComplementaryGray
import com.multimoney.multimoney.presentation.theme.GradientGrey1
import com.multimoney.multimoney.presentation.theme.GradientGrey2
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography

/**
 * RoundedPaymentButton: This Button is used to select the amount that the people will pay
 *
 * Parameters:
 * @param modifier: Apply style.
 * @param onClick: This function will be executed when de user tap the button.
 * @param strokeWidth: Variable to define the stroke width.
 * @param roundedShapeDp: Variable to define how many dp the corners will have.
 * @param mainText: Is the main text that will display the amount.
 * @param secondaryText: Is the secondary text that will display the description.
 * @param isSelected: This variable will decide what color should be displayed in the stroke.
 * @param textAlign: The alignment for main a secondary text.
 * **/

@Composable
fun RoundedPaymentButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    strokeWidth: Dp,
    roundedShapeDp: Dp,
    mainText: String,
    secondaryText: String? = null,
    isSelected: Boolean,
    isSingleLine: Boolean = false,
    textAlign: Alignment.Horizontal = Alignment.Start
) {
    // Set colors depending on system theme
    val selectedColors: List<Color>
    val unselectedColors: List<Color>
    val backgroundColor: Color
    val mainTextColor: Color
    val secondaryTextColor: Color

    if (isSystemInDarkTheme()) {
        unselectedColors = listOf(
            GradientGrey1,
            GradientGrey2
        )
        selectedColors = listOf(
            MultimoneyTheme.colors.primary,
            MultimoneyTheme.colors.primary
        )
        backgroundColor = ComplementaryBlack
        mainTextColor = MultimoneyTheme.colors.textLink
        secondaryTextColor = ComplementaryGray
    } else {
        unselectedColors = listOf(
            GradientGrey1,
            GradientGrey2
        )
        selectedColors = listOf(
            MultimoneyTheme.colors.primary,
            MultimoneyTheme.colors.primary
        )
        backgroundColor = ComplementaryBlack
        mainTextColor = MultimoneyTheme.colors.textLink
        secondaryTextColor = ComplementaryGray
    }

    Card(
        modifier = modifier
            .clickable {
                onClick()
            },
        border = BorderStroke(
            width = strokeWidth,
            brush = Brush.verticalGradient(
                colors = if (isSelected) {
                    selectedColors
                } else {
                    unselectedColors
                }
            )
        ),
        shape = RoundedCornerShape(roundedShapeDp),
        backgroundColor = backgroundColor
    ) {
        if (isSingleLine) {
            Row(
                modifier = Modifier
                    .wrapContentSize().padding(horizontal = 30.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = mainText,
                    style = Typography.subtitle1.copy(fontWeight = FontWeight.W700),
                    color = mainTextColor,
                    textAlign = TextAlign.Center
                )
                secondaryText?.let {
                    Text(
                        modifier = Modifier.padding(6.dp),
                        text = secondaryText,
                        style = Typography.subtitle2,
                        color = secondaryTextColor,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = textAlign
            ) {
                Text(
                    text = mainText,
                    style = Typography.subtitle1.copy(fontWeight = FontWeight.W700),
                    color = mainTextColor,
                    textAlign = TextAlign.Left
                )
                secondaryText?.let {
                    Text(
                        text = secondaryText,
                        style = Typography.caption,
                        color = secondaryTextColor,
                        textAlign = TextAlign.Left
                    )
                }
            }
        }
    }
}
