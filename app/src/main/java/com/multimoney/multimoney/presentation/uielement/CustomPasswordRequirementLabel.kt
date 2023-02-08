package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.presentation.theme.GrayScale300
import com.multimoney.multimoney.presentation.theme.GrayScale500
import com.multimoney.multimoney.presentation.theme.SemanticNegative400
import com.multimoney.multimoney.presentation.theme.SemanticNegative500
import com.multimoney.multimoney.presentation.theme.SemanticPositive400
import com.multimoney.multimoney.presentation.theme.SemanticPositive600
import com.multimoney.multimoney.presentation.theme.Typography

/**
 * CustomPasswordRequirementText: This Text is used to match design system
 *
 * Parameters:
 * @param modifier: Apply style.
 * @param text: Text next to the icon
 * @param successIcon: Icon when is success
 * @param errorIcon: Icon when is error
 * @param state: flag to handle the different states
 *
 */
@Composable
fun CustomPasswordRequirementLabel(
    modifier: Modifier = Modifier,
    text: String,
    successIcon: Int,
    errorIcon: Int,
    state: Boolean?
) {

    val statusColor: Color
    val icon: Int

    if (isSystemInDarkTheme()) {
        when (state) {
            null -> {
                statusColor = GrayScale300
                icon = errorIcon
            }
            true -> {
                statusColor = SemanticPositive400
                icon = successIcon
            }
            false -> {
                statusColor = SemanticNegative400
                icon = errorIcon
            }
        }
    } else {
        when (state) {
            null -> {
                statusColor = GrayScale300
                icon = errorIcon
            }
            true -> {
                statusColor = SemanticPositive400
                icon = successIcon
            }
            false -> {
                statusColor = SemanticNegative400
                icon = errorIcon
            }
        }
    }
    Box(modifier = modifier) {
        Row(modifier = Modifier.padding(4.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = "",
                tint = statusColor,
                modifier = Modifier.padding(end = 4.dp)
            )
            Text(text = text, color = statusColor, style = Typography.caption)
        }
    }
}