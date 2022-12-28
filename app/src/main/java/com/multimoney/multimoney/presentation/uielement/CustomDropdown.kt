package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.Popup
import com.multimoney.domain.model.credit.CreditCatalogOption
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.extension.findActivity
import com.multimoney.multimoney.presentation.theme.BlackTransparency70
import com.multimoney.multimoney.presentation.theme.ComplementaryBlack3
import com.multimoney.multimoney.presentation.theme.DefaultWhite
import com.multimoney.multimoney.presentation.theme.GrayScale400
import com.multimoney.multimoney.presentation.theme.GrayScale500
import com.multimoney.multimoney.presentation.theme.GrayScale600
import com.multimoney.multimoney.presentation.theme.GrayScale700
import com.multimoney.multimoney.presentation.theme.GrayScale800
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Primary300
import com.multimoney.multimoney.presentation.theme.Primary400
import com.multimoney.multimoney.presentation.theme.Primary500
import com.multimoney.multimoney.presentation.theme.SemanticNegative500
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.theme.WhiteTransparency70
import com.multimoney.multimoney.presentation.util.gesture.detectTapAndPressUnconsumed

@Composable
fun CustomDropdown(
    modifier: Modifier,
    items: List<String>,
    value: String,
    onValueChange: (newText: String) -> Unit = {},
    labelText: String,
    placeHolder: String?,
    isError: Boolean = false,
    enabled: Boolean = true
) {
    val context = LocalContext.current
    val activity = context.findActivity()

    val labelColor: Color
    var backgroundColor: Color
    val iconTintColor: Color
    val textColor: Color
    val focusedColor: Color
    var expanded by remember { mutableStateOf(false) }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }
    val icon = R.drawable.ic_dropdown_close

    if (isSystemInDarkTheme()) {
        focusedColor = GrayScale700
        labelColor = WhiteTransparency70
        backgroundColor = GrayScale700
        when {
            isError -> {
                iconTintColor = GrayScale400
                textColor = DefaultWhite
            }
            enabled -> {
                iconTintColor = GrayScale400
                textColor = DefaultWhite
            }
            else -> {
                backgroundColor = GrayScale500
                iconTintColor = GrayScale400
                textColor = GrayScale400
            }
        }
    } else {
        focusedColor = Primary300
        labelColor = GrayScale500
        backgroundColor = DefaultWhite
        when {
            isError -> {
                iconTintColor = Primary400
                textColor = GrayScale800
            }
            enabled -> {
                iconTintColor = Primary500
                textColor = GrayScale600
            }
            else -> {
                backgroundColor = DefaultWhite
                iconTintColor = Primary500
                textColor = GrayScale400
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
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .onGloballyPositioned { coordinates ->
                    textFieldSize = coordinates.size.toSize()
                }
                .clickable {
                    expanded = !expanded
                },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = backgroundColor,
                focusedIndicatorColor = focusedColor,
                unfocusedIndicatorColor = GrayScale400,
                errorIndicatorColor = SemanticNegative500,
                textColor = textColor
            ),
            trailingIcon = {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = "",
                    tint = iconTintColor,
                    modifier = Modifier.clickable { expanded = !expanded }
                )
            },
            placeholder = {
                Text(
                    text = placeHolder ?: "",
                    color = GrayScale400,
                    style = Typography.body2
                )
            },
            enabled = false
        )

        /*
        * Show Popup when dropdown is expanded to fill background with black transparent color
        * Popup is used to not break the view hierarchy
        */
        if (expanded) {
            Popup {
                Box(modifier = Modifier
                    .background(BlackTransparency70)
                    .fillMaxSize())
            }
        }

        MaterialTheme(shapes = MaterialTheme.shapes.copy(medium = RoundedCornerShape(16.dp))) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .width(with(LocalDensity.current) { textFieldSize.width.toDp() })
                    .background(ComplementaryBlack3)
                    .pointerInput(Unit) {
                        detectTapAndPressUnconsumed(onTap = {
                            activity?.onUserInteraction()
                        })
                    },
                offset = DpOffset(0.dp, 10.dp)
            ) {
                items.forEach { label ->
                    DropdownMenuItem(onClick = {
                        expanded = false
                        onValueChange(label)
                    }) {
                        Text(
                            text = label,
                            style = Typography.body2.copy(
                                color = MultimoneyTheme.colors.text,
                                fontWeight = FontWeight.Normal
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CustomDropdown(
    modifier: Modifier,
    items: List<CreditCatalogOption?>?,
    value: CreditCatalogOption?,
    onValueChange: (newText: CreditCatalogOption?) -> Unit = {},
    labelText: String,
    placeHolder: String?,
    isError: Boolean = false,
    enabled: Boolean = true
) {
    CustomDropdown(
        modifier = modifier,
        items = items?.map { it?.description ?: "" } ?: listOf(),
        value = value?.description ?: "",
        onValueChange = { valueSelected ->
            onValueChange(items?.findLast { it?.description == valueSelected })
        },
        labelText = labelText,
        placeHolder = placeHolder,
        isError = isError,
        enabled = enabled
    )
}
