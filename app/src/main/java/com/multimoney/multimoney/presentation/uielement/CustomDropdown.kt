package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.Icon
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.*

@Composable
fun CustomDropdown(
    modifier: Modifier,
    items: List<String>,
    value: String,
    onValueChange: (newText: String) -> Unit = {},
    labelText: String,
    placeHolder: String?,
    isError: Boolean = false,
    enabled: Boolean = true,
) {
    val labelColor: Color
    var backgroundColor: Color
    val iconTintColor: Color
    val textColor: Color
    var expanded by remember { mutableStateOf(false) }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }
    val icon = if (expanded) R.drawable.ic_dropdown_close else R.drawable.ic_dropdown_open

    if (isSystemInDarkTheme()) {
        labelColor = GrayScale300
        backgroundColor = GrayScale600
        when {
            isError -> {
                iconTintColor = Primary400
                textColor = DefaultWhite
            }
            enabled -> {
                iconTintColor = Primary400
                textColor = DefaultWhite
            }
            else -> {
                backgroundColor = GrayScale500
                iconTintColor = GrayScale400
                textColor = GrayScale400
            }
        }
    } else {
        labelColor = GrayScale500
        backgroundColor = DefaultWhite
        when {
            isError -> {
                iconTintColor = Primary400
                textColor = GrayScale800
            }
            enabled -> {
                iconTintColor = Primary400
                textColor = GrayScale800
            }
            else -> {
                backgroundColor = GrayScale300
                iconTintColor = GrayScale500
                textColor = GrayScale500
            }
        }
    }

    Column(
        modifier = modifier
    ) {
        Text(
            text = labelText,
            color = labelColor,
            style = Typography.body2
        )
        OutlinedTextField(
            value = value,
            onValueChange = {
            },
            shape = RoundedCornerShape(25),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .onGloballyPositioned { coordinates ->
                    textFieldSize = coordinates.size.toSize()
                },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = backgroundColor,
                focusedIndicatorColor = Primary500,
                unfocusedIndicatorColor = GrayScale400,
                errorIndicatorColor = SemanticNegative500,
                textColor = textColor
            ),
            trailingIcon = {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = "",
                    tint = iconTintColor,
                    modifier = Modifier.clickable { expanded = !expanded })
            },
            placeholder = {
                Text(
                    text = placeHolder ?: "",
                    color = GrayScale500,
                    style = Typography.body2
                )
            }

        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .padding(top = 8.dp)
                .width(with(LocalDensity.current) { textFieldSize.width.toDp() })
        ) {
            items.forEach { label ->
                DropdownMenuItem(onClick = {
                    expanded = false
                    onValueChange(label)
                }) {
                    Text(
                        text = label, style = Typography.body2.copy(
                            color = MultimoneyTheme.colors.text,
                            fontWeight = FontWeight.Normal
                        )
                    )
                }
            }
        }
    }
}