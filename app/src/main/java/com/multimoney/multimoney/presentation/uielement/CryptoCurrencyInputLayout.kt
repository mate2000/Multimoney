package com.multimoney.multimoney.presentation.uielement

import android.icu.text.DecimalFormat
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography

@OptIn(ExperimentalComposeUiApi::class)
@ExperimentalAnimationApi
@Composable
fun CryptoCurrencyInputLayout(
    modifier: Modifier = Modifier,
    query: MutableState<String>,
    focused: MutableState<Boolean>,
    iconCurrency: String,
    onSearchClick: () -> Unit

) {

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    keyboardController?.show()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        CustomTextField(
            focused = focused,
            value = query,
            iconCurrency = iconCurrency,
            onSearchClick = {
                onSearchClick()
            }
        )
    }
}



@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    focused: MutableState<Boolean>,
    value: MutableState<String>,
    iconCurrency: String,
    onSearchClick: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val isTransformationCurrency = remember { mutableStateOf(false) }
    //focusRequester.requestFocus()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        contentAlignment = Alignment.Center
    ) {
        OutlinedTextField(
            modifier = modifier
                .fillMaxWidth()
                .height(80.dp)
                .onFocusChanged { focused.value = it.isFocused }
                .focusRequester(focusRequester),
            value = value.value,
            textStyle = Typography.h4.copy(
                color = MultimoneyTheme.colors.text,
                fontWeight = FontWeight.Bold,
                fontSize = when {
                    value.value.length <= 10 -> 34.sp
                    value.value.length <= 17 -> 24.sp
                    value.value.length <= 26 -> 16.sp
                    else -> 12.sp
                }
            ),
            onValueChange = { value.value = it },
            placeholder = {
                Text(
                    text = "$0",
                    style = Typography.h4.copy(
                        color = MultimoneyTheme.colors.bodyTextColor,
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            visualTransformation = if (isTransformationCurrency.value) {
                VisualTransformation.None
            } else {
                VisualTransformation.None
            },
            shape = RoundedCornerShape(50.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            keyboardActions = KeyboardActions { onSearchClick() },
            colors = TextFieldDefaults.textFieldColors(
                textColor = MultimoneyTheme.colors.text,
                cursorColor = MultimoneyTheme.colors.text,
                disabledTextColor = MultimoneyTheme.colors.fullTransparency,
                backgroundColor = MultimoneyTheme.colors.backgroundInformativeChip,
                focusedIndicatorColor = MultimoneyTheme.colors.fullTransparency,
                unfocusedIndicatorColor = MultimoneyTheme.colors.fullTransparency,
                disabledIndicatorColor = MultimoneyTheme.colors.fullTransparency
            )
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 28.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            IconButton(
                modifier = Modifier
                    .width(28.dp)
                    .height(48.dp),
                onClick = { isTransformationCurrency.value = !isTransformationCurrency.value }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        modifier = Modifier.padding(bottom = 8.dp),
                        painter = painterResource(id = R.drawable.ic_currency_invert_arrows),
                        tint = MultimoneyTheme.colors.textLink,
                        contentDescription = null
                    )
                    Text(
                        text = if (isTransformationCurrency.value) "USD" else iconCurrency,
                        style = Typography.body2.copy(
                            color = MultimoneyTheme.colors.textLink,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }

}