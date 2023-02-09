package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.extension.findActivity
import com.multimoney.multimoney.presentation.theme.DefaultWhite
import com.multimoney.multimoney.presentation.theme.GrayScale500
import com.multimoney.multimoney.presentation.theme.GrayScale700
import com.multimoney.multimoney.presentation.theme.GrayScale800
import com.multimoney.multimoney.presentation.theme.Primary500
import com.multimoney.multimoney.presentation.theme.SemanticInformative400
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.theme.WhiteTransparency80
import com.multimoney.multimoney.presentation.util.gesture.detectTapAndPressUnconsumed

/**
 * CustomDialog: This Dialog is used to match design system
 *
 * Parameters:
 * @param modifier: Apply style.
 * @param title: Text for title of the Dialog
 * @param message: Text message of the Dialog
 * @param positiveButtonText: Text for the positive button
 * @param negativeButtonText: Text for the negative button
 * @param positiveButtonColor: Color for the positive button
 * @param negativeButtonColor: Color for the negative button
 * @param topIcon: Resource for the top icon
 * @param shape: shape for the dialog
 * @param onNegativeAction: Function to handle the negative button click
 * @param onPositiveAction: Function to handle the positive button click
 * @param onDismissAction: : Function to handle the dismiss event
 * @param openDialogCustom: Variable tu handle the state of the Dialog
 */
@Composable
fun CustomDialog(
    modifier: Modifier = Modifier,
    title: String = "",
    message: String = "",
    positiveButtonText: String = stringResource(id = R.string.custom_dialog_default_positive_label),
    negativeButtonText: String = "",
    positiveButtonColor: Color = SemanticInformative400,
    negativeButtonColor: Color = SemanticInformative400,
    topIcon: Int? = null,
    shape: Shape = MaterialTheme.shapes.medium,
    onNegativeAction: () -> Unit = {},
    onPositiveAction: () -> Unit = {},
    onDismissAction: () -> Unit = {},
    openDialogCustom: MutableState<Boolean> = mutableStateOf(false),
    isCancelable: Boolean = true
) {
    val context = LocalContext.current
    val activity = context.findActivity()

    // Colors
    val titleColor: Color
    val messageColor: Color
    val positiveButtonTextColor: Color
    val negativeButtonTextColor: Color
    val iconTintColor: Color
    val backgroundColor: Color
    if (isSystemInDarkTheme()) {
        titleColor = DefaultWhite
        messageColor = WhiteTransparency80
        positiveButtonTextColor = positiveButtonColor
        negativeButtonTextColor = negativeButtonColor
        iconTintColor = DefaultWhite
        backgroundColor = GrayScale700
    } else {
        titleColor = DefaultWhite
        messageColor = WhiteTransparency80
        positiveButtonTextColor = positiveButtonColor
        negativeButtonTextColor = negativeButtonColor
        iconTintColor = DefaultWhite
        backgroundColor = GrayScale700
    }

    // Paddings
    var topPaddingTitle: Dp = 14.5.dp
    val topPaddingMessage: Dp
    var topPaddingButtons: Dp = 18.dp
    if (topIcon == null && title.isNotEmpty()) {
        topPaddingTitle = 24.dp
        topPaddingMessage = 15.dp
    } else if (topIcon == null && title.isEmpty()) {
        topPaddingMessage = 23.dp
    } else {
        topPaddingMessage = 13.dp
        topPaddingButtons = 34.dp
    }

    Dialog(onDismissRequest = {
        if (isCancelable) {
            onDismissAction()
            openDialogCustom.value = false
        }
    }) {
        Card(
            shape = shape,
            backgroundColor = backgroundColor,
            modifier = Modifier
                .defaultMinSize(minHeight = 123.dp)
                .fillMaxWidth().pointerInput(Unit) {
                    detectTapAndPressUnconsumed(
                        onTap = {
                            activity?.onUserInteraction()
                        }
                    )
                }
        ) {
            Column(
                modifier = Modifier.padding(start = 24.dp, end = 20.dp, bottom = 18.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    if (topIcon != null) {
                        Icon(
                            painter = painterResource(topIcon),
                            contentDescription = "",
                            tint = iconTintColor,
                            modifier = modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 14.dp)
                        )
                    }
                    if (title.isNotEmpty()) {
                        var textStyle = Typography.subtitle1
                        if (topIcon != null) {
                            textStyle = textStyle.copy(textAlign = TextAlign.Center)
                        }
                        Text(
                            text = title,
                            modifier.padding(top = topPaddingTitle),
                            color = titleColor,
                            style = textStyle
                        )
                    }
                    Text(
                        text = message,
                        Modifier.padding(end = 4.dp, top = topPaddingMessage),
                        color = messageColor,
                        style = Typography.body2
                    )
                }
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = topPaddingButtons)
                ) {
                    if (negativeButtonText.isNotEmpty()) {
                        ClickableText(
                            text = AnnotatedString(negativeButtonText),
                            onClick = {
                                onNegativeAction()
                                openDialogCustom.value = false
                            },
                            style = Typography.button.copy(
                                fontWeight = SemiBold,
                                color = negativeButtonTextColor
                            )
                        )
                    }
                    ClickableText(
                        modifier = Modifier.padding(start = 32.dp),
                        text = AnnotatedString(positiveButtonText),
                        onClick = {
                            onPositiveAction()
                            openDialogCustom.value = false
                        },
                        style = Typography.button.copy(
                            fontWeight = SemiBold,
                            color = positiveButtonTextColor
                        )
                    )
                }
            }
        }
    }
}
