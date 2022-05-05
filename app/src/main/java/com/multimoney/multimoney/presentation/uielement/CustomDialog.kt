package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.DefaultWhite
import com.multimoney.multimoney.presentation.theme.GrayScale500
import com.multimoney.multimoney.presentation.theme.GrayScale700
import com.multimoney.multimoney.presentation.theme.GrayScale800
import com.multimoney.multimoney.presentation.theme.Primary400
import com.multimoney.multimoney.presentation.theme.Primary500
import com.multimoney.multimoney.presentation.theme.WhiteTransparency80

@Composable
fun CustomAlertDialog(
    modifier: Modifier = Modifier,
    title: String = "",
    message: String = "",
    positiveText: String = stringResource(id = R.string.custom_dialog_default_positive_label),
    negativeText: String = stringResource(id = R.string.custom_dialog_default_negative_label),
    topIcon: Int? = null,
    shape: Shape = MaterialTheme.shapes.medium,
    onPositiveAction: () -> Unit,
    onNegativeAction: () -> Unit,
    onDismissAction: () -> Unit
) {

    val titleColor: Color
    val messageColor: Color
    val buttonColor: Color
    val iconTintColor: Color
    val backgroundColor: Color
    if (isSystemInDarkTheme()) {
        titleColor = DefaultWhite
        messageColor = WhiteTransparency80
        buttonColor = Primary400
        iconTintColor = DefaultWhite
        backgroundColor = GrayScale700
    } else {
        titleColor = GrayScale800
        messageColor = GrayScale500
        buttonColor = Primary500
        iconTintColor = Primary500
        backgroundColor = DefaultWhite
    }

    Dialog(onDismissRequest = onDismissAction) {
        Card(shape = shape, backgroundColor = backgroundColor, modifier = Modifier.padding()) {

        }
    }

}