package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme

/*
* like radio button without "radio icon" to use with for o lazy column or row
* */

@Composable
fun CustomSelector(
    text: String,
    selected: Boolean = false,
    onOptionSelected: () -> Unit
) {

    val color = if (selected) MultimoneyTheme.colors.text else MultimoneyTheme.colors.quickActionLabelColor

    Column(modifier = Modifier
        .wrapContentWidth()
        .wrapContentHeight()
        .background(MultimoneyTheme.colors.fullTransparency),
        verticalArrangement = Arrangement.Center
    ) {
        TextButton(
            onClick = onOptionSelected,
            colors = ButtonDefaults.buttonColors(backgroundColor = MultimoneyTheme.colors.fullTransparency),
            modifier = Modifier.wrapContentWidth()
        ) {
            Text(text = text, color = color)
        }
    }
}