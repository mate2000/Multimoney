package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.Primary400
import com.multimoney.multimoney.presentation.theme.Typography

/**
 * CustomTextButton: This component is used to create text buttons*
 * Parameters:
 * @param textResource: Text to convert into button
 * @param endIconResource: End Icon to complement button
 * @param onClick: This will happen when user perform click on text button
 */

@Composable
fun CustomTextButton(
    textResource: Int = R.string.empty,
    endIconResource: Int = 0,
    onClick: () -> Unit = {}
) {
    val textColor = if (isSystemInDarkTheme()) {
        Primary400
    } else {
        Primary400
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(id = textResource),
            modifier = Modifier.clickable {
                onClick()
            },
            style = Typography.button.copy(fontSize = 14.sp),
            color = textColor
        )
        Spacer(modifier = Modifier.width(7.dp))
        if (endIconResource != 0) {
            Image(
                modifier = Modifier.size(20.dp),
                painter = painterResource(id = endIconResource),
                contentDescription = ""
            )
        }
    }
}
