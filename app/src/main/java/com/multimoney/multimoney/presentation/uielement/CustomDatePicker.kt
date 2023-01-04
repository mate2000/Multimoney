package com.multimoney.multimoney.presentation.uielement

import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ireward.htmlcompose.HtmlText
import com.multimoney.multimoney.R
import com.multimoney.multimoney.R.drawable
import com.multimoney.multimoney.presentation.extension.findActivity
import com.multimoney.multimoney.presentation.theme.DefaultBlack
import com.multimoney.multimoney.presentation.theme.DefaultWhite
import com.multimoney.multimoney.presentation.theme.GrayScale300
import com.multimoney.multimoney.presentation.theme.GrayScale400
import com.multimoney.multimoney.presentation.theme.GrayScale500
import com.multimoney.multimoney.presentation.theme.GrayScale800
import com.multimoney.multimoney.presentation.theme.Primary500
import com.multimoney.multimoney.presentation.theme.SemanticNegative400
import com.multimoney.multimoney.presentation.theme.SemanticNegative500
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.theme.WhiteTransparency10
import com.multimoney.multimoney.presentation.theme.WhiteTransparency30
import com.multimoney.multimoney.presentation.theme.WhiteTransparency60
import com.multimoney.multimoney.presentation.theme.WhiteTransparency70
import com.multimoney.multimoney.presentation.theme.WhiteTransparency90
import java.util.Calendar
import java.util.Date
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CustomDatePicker(
    context: Context,
    modifier: Modifier = Modifier,
    labelText: String?,
    value: String? = null,
    minYear: Int,
    minMonth: Int,
    minDay: Int,
    leadingIcon: Int? = null,
    leadingIconComposable: @Composable ((Color) -> Unit)? = null,
    trailingIcon: Int? = null,
    placeHolder: String = "",
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    errorMessage: String? = null,
    isError: Boolean = false,
    onValueChange: (view: DatePicker, year: Int, Month: Int, dayOfMonth: Int) -> Unit,
    enabled: Boolean = true
) {
    val focusManager = LocalFocusManager.current
    val activity = context.findActivity()
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()

    // Set colors depending on system theme
    val labelColor: Color
    var backgroundColor: Color
    val iconTintColor: Color
    val textColor: Color
    val placeholderColor: Color
    val focusedIndicatorColor: Color
    val unfocusedIndicatorColor: Color
    val errorIndicatorColor: Color

    if (isSystemInDarkTheme()) {
        labelColor = WhiteTransparency70
        backgroundColor = WhiteTransparency10
        placeholderColor = WhiteTransparency30
        unfocusedIndicatorColor = DefaultBlack
        errorIndicatorColor = if (isError) {
            SemanticNegative400
        } else {
            WhiteTransparency90
        }
        when {
            isError -> {
                focusedIndicatorColor = SemanticNegative400
                iconTintColor = SemanticNegative400
                textColor = WhiteTransparency90
            }
            enabled -> {
                focusedIndicatorColor = WhiteTransparency60
                iconTintColor = WhiteTransparency60
                textColor = WhiteTransparency90
            }
            else -> {
                focusedIndicatorColor = DefaultBlack
                backgroundColor = GrayScale500
                iconTintColor = WhiteTransparency60
                textColor = WhiteTransparency30
            }
        }
    } else {
        labelColor = DefaultWhite
        backgroundColor = WhiteTransparency10
        placeholderColor = GrayScale500
        unfocusedIndicatorColor = GrayScale400
        errorIndicatorColor = if (isError) {
            SemanticNegative500
        } else {
            GrayScale800
        }
        when {
            isError -> {
                focusedIndicatorColor = SemanticNegative500
                iconTintColor = SemanticNegative500
                textColor = GrayScale800
            }
            enabled -> {
                focusedIndicatorColor = Primary500
                iconTintColor = Primary500
                textColor = GrayScale800
            }
            else -> {
                focusedIndicatorColor = GrayScale400
                backgroundColor = GrayScale300
                iconTintColor = GrayScale500
                textColor = GrayScale500
            }
        }
    }

    Column(modifier = modifier.wrapContentHeight()) {
        // Display label is it isn't null
        labelText?.let {
            Text(
                text = labelText,
                color = labelColor,
                style = Typography.body2
            )
        }

        // Display textField
        OutlinedTextField(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
                .clickable {
                    focusManager.clearFocus()
                    val calendar = Calendar.getInstance()
                    val datePicker = DatePickerDialog(
                        context,
                        R.style.MyDatePickerStyle,
                        { view, year, month, day ->
                            activity?.onUserInteraction()
                            onValueChange(view, year, month, day)
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    )
                    calendar.set(
                        minYear,
                        minMonth,
                        minDay
                    )
                    datePicker.datePicker.minDate = calendar.timeInMillis
                    datePicker.datePicker.maxDate = Date().time
                    datePicker.show()
                }
                .bringIntoViewRequester(bringIntoViewRequester)
                .onFocusChanged {
                    if (it.isFocused) {
                        coroutineScope.launch {
                            // This sends a request to all parents that asks them to scroll so
                            // that this item is brought into view.
                            bringIntoViewRequester.bringIntoView()
                        }
                    }
                },
            value = value ?: "",
            shape = RoundedCornerShape(50),
            leadingIcon = leadingIcon?.let {
                {
                    Icon(
                        painter = painterResource(id = it),
                        contentDescription = "",
                        modifier = Modifier.padding(start = 8.dp),
                        tint = iconTintColor
                    )
                }
            } ?: leadingIconComposable?.let {
                { it(iconTintColor) }
            },
            trailingIcon = trailingIcon?.let {
                {
                    Icon(
                        painter = painterResource(id = it),
                        contentDescription = "",
                        tint = iconTintColor
                    )
                }
            },
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            onValueChange = {},
            placeholder = {
                Text(
                    text = placeHolder,
                    color = placeholderColor,
                    style = Typography.body2
                )
            },
            isError = isError,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = backgroundColor,
                focusedIndicatorColor = focusedIndicatorColor,
                unfocusedIndicatorColor = unfocusedIndicatorColor,
                errorIndicatorColor = errorIndicatorColor,
                textColor = textColor,
                disabledTextColor = textColor,
                cursorColor = textColor
            ),
            enabled = false,
            textStyle = Typography.body2
        )

        // Display error message
        if (isError && errorMessage.isNullOrBlank().not()) {
            Row(
                modifier = Modifier.padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = drawable.ic_exclamation_mark),
                    modifier = Modifier
                        .size(width = 11.dp, height = 11.dp),
                    contentDescription = "",
                    tint = errorIndicatorColor
                )
                HtmlText(
                    text = if (errorMessage.isNullOrBlank().not()) {
                        errorMessage ?: ""
                    } else {
                        ""
                    },
                    modifier = Modifier
                        .padding(start = 5.dp)
                        .wrapContentSize(),
                    style = Typography.caption.copy(color = errorIndicatorColor)
                )
            }
        }
    }
}
