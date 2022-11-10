package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.VisualTransformation
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.extension.findActivity
import com.multimoney.multimoney.presentation.theme.DefaultBlack
import com.multimoney.multimoney.presentation.theme.GrayScale300
import com.multimoney.multimoney.presentation.theme.GrayScale400
import com.multimoney.multimoney.presentation.theme.GrayScale500
import com.multimoney.multimoney.presentation.theme.GrayScale800
import com.multimoney.multimoney.presentation.theme.Primary500
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.theme.WhiteTransparency10
import com.multimoney.multimoney.presentation.theme.WhiteTransparency30
import com.multimoney.multimoney.presentation.theme.WhiteTransparency60
import com.multimoney.multimoney.presentation.theme.WhiteTransparency90
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CustomSearchBar(
    modifier: Modifier = Modifier,
    value: String? = null,
    placeHolder: String = "",
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions = KeyboardActions(onDone = {
    }),
    enabled: Boolean = true,
    onValueChange: (newText: String) -> Unit = {},
    customTransformation: VisualTransformation? = null,
    onDebounceValidation: (newText: String) -> Unit = {}
) {
    val context = LocalContext.current
    val activity = context.findActivity()

    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()

    val textDebounce = remember { MutableStateFlow("") }
    val textDebounceFlow: Flow<String> = remember {
        textDebounce.debounce(500)
            .distinctUntilChanged()
            .flatMapLatest {
                if (it.isNotEmpty()) {
                    onDebounceValidation(it)
                }
                flowOf(it)
            }
    }

    // Set colors depending on system theme
    var backgroundColor: Color
    val textColor: Color
    val placeholderColor: Color
    val focusedIndicatorColor: Color
    val unfocusedIndicatorColor: Color

    if (isSystemInDarkTheme()) {
        backgroundColor = WhiteTransparency10
        placeholderColor = WhiteTransparency30
        unfocusedIndicatorColor = DefaultBlack
        when {
            enabled -> {
                focusedIndicatorColor = WhiteTransparency60
                textColor = WhiteTransparency90
            }
            else -> {
                focusedIndicatorColor = DefaultBlack
                backgroundColor = GrayScale500
                textColor = WhiteTransparency30
            }
        }
    } else {
        backgroundColor = WhiteTransparency10
        placeholderColor = GrayScale500
        unfocusedIndicatorColor = GrayScale400
        when {
            enabled -> {
                focusedIndicatorColor = Primary500
                textColor = GrayScale800
            }
            else -> {
                focusedIndicatorColor = GrayScale400
                backgroundColor = GrayScale300
                textColor = GrayScale500
            }
        }
    }

    Column(modifier = modifier) {
        // Display textField
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
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
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            onValueChange = {
                activity?.onUserInteraction()
                onValueChange(it)
                textDebounce.value = it
            },
            placeholder = {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = placeHolder,
                    color = placeholderColor,
                    style = Typography.body2
                )
            },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = backgroundColor,
                focusedIndicatorColor = backgroundColor,
                unfocusedIndicatorColor = unfocusedIndicatorColor,
                textColor = textColor,
                cursorColor = textColor
            ),
            enabled = enabled,
            visualTransformation = customTransformation ?: VisualTransformation.None,
            textStyle = Typography.body2,
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search),
                    tint = textColor,
                    contentDescription = "Search Icon"
                )
            }

        )

        // This is required to execute the debounce
        val textDebounceFlowValue by textDebounceFlow.collectAsState("")
    }
}
