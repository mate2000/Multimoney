package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.presentation.theme.GrayScale800
import com.multimoney.multimoney.presentation.theme.Primary400
import com.multimoney.multimoney.presentation.theme.WhiteTransparency12
import com.multimoney.multimoney.presentation.theme.WhiteTransparency50
import com.multimoney.multimoney.presentation.theme.WhiteTransparency60

/**
 * CustomRadioButton: Selector that forces the user to only pick one option
 *
 * Parameters
 * @param modifier: Dimensions for the toggle column.
 * @param selectedIndex: Selected index from the list.
 * @param items: Items to be displayed as toggle.
 * @param onIndexChanged: Function that handles selected option.
 * */
@Composable
@Preview
fun CustomToggleButton(
    modifier: Modifier = Modifier,
    selectedIndex: Int = 0,
    items: List<String> = listOf("$", "&"),
    onIndexChanged: (Int) -> Unit = {}
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .height(40.dp)
                .wrapContentWidth()
                .border(border = BorderStroke(1.dp, WhiteTransparency50), shape = RoundedCornerShape(15.dp))
        ) {
            items.forEachIndexed { index, item ->
                OutlinedButton(
                    modifier = Modifier
                        .height(40.dp)
                        .wrapContentWidth(),
                    shape = when (index) {
                        0 -> RoundedCornerShape(topStart = 15.dp, bottomStart = 15.dp)
                        items.lastIndex -> RoundedCornerShape(topEnd = 15.dp, bottomEnd = 15.dp)
                        else -> RoundedCornerShape(topStart = 0.dp, bottomStart = 0.dp, topEnd = 0.dp, bottomEnd = 0.dp)
                    },
                    onClick = { onIndexChanged(index) },
                    colors = if (selectedIndex == index) {
                        // selected colors
                        ButtonDefaults.outlinedButtonColors(backgroundColor = Primary400, contentColor = Primary400)
                    } else {
                        // not selected colors
                        ButtonDefaults.outlinedButtonColors(
                            backgroundColor = WhiteTransparency12,
                            contentColor = WhiteTransparency12
                        )
                    }
                ) {
                    Text(
                        text = item,
                        color = if (selectedIndex == index) {
                            GrayScale800
                        } else {
                            WhiteTransparency60
                        },
                    )
                }
            }
        }
    }
}