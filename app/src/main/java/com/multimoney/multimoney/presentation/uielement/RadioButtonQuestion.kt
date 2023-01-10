package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.DefaultWhite
import com.multimoney.multimoney.presentation.theme.GrayScale500
import com.multimoney.multimoney.presentation.theme.GrayScale800
import com.multimoney.multimoney.presentation.theme.Primary500
import com.multimoney.multimoney.presentation.theme.SemanticInformative400
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.theme.WhiteTransparency70
import com.multimoney.multimoney.presentation.theme.WhiteTransparency90

/**
 * CustomDialog: This Dialog is used to match design system
 *
 * Parameters:
 * @param questionTextResource: Question resource. If it has disclaimer this resource must contain the disclaimer text
 * @param firstButtonTextResource: Top Radio bottom text resource
 * @param firstButtonTextResource: Bottom Radio bottom text resource
 * @param shouldHaveDisclaimer: Boolean to decide if should we display a disclaimer
 * @param disclaimerTextResource: Disclaimer text Resource value
 * @param firstButtonIsSelected: Boolean top button to know if is selected or not
 * @param secondButtonIsSelected: Boolean bottom button to know if is selected or not
 * @param onDisclaimerClick: Function to do when disclaimer is clicked
 * @param onFirstButtonOnClick: Function to do when top button is clicked
 * @param onSecondButtonOnClick: Function to do when top bottom is clicked
 */

@Composable
@Preview
fun RadioButtonQuestion(
    questionTextResource: Int = R.string.empty,
    firstButtonTextResource: Int = R.string.empty,
    secondButtonTextResource: Int = R.string.empty,
    shouldHaveDisclaimer: Boolean = false,
    disclaimerTextResource: Int = R.string.empty,
    firstButtonIsSelected: Boolean = false,
    secondButtonIsSelected: Boolean = false,
    onDisclaimerClick: () -> Unit = {},
    onFirstButtonOnClick: () -> Unit = {},
    onSecondButtonOnClick: () -> Unit = {}
) {
    val backgroundColor: Color
    val disclaimerColor: Color
    val labelColor: Color
    if (isSystemInDarkTheme()) {
        backgroundColor = GrayScale800
        disclaimerColor = SemanticInformative400
        labelColor = WhiteTransparency90
    } else {
        backgroundColor = GrayScale800
        disclaimerColor = SemanticInformative400
        labelColor = WhiteTransparency90
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (shouldHaveDisclaimer) {
                val annotatedString = buildAnnotatedString {
                    val text = stringResource(id = questionTextResource)
                    withStyle(
                        style = Typography.body1.copy(
                            color = labelColor
                        ).toSpanStyle()
                    ) {
                        append(text)
                    }

                    val disclaimerText = stringResource(id = disclaimerTextResource)
                    val startIndex = text.indexOf(disclaimerText)
                    val endIndex = startIndex + disclaimerText.length
                    addStyle(
                        style = Typography.body1.copy(color = disclaimerColor).toSpanStyle(),
                        start = startIndex,
                        end = endIndex
                    )

                    addStringAnnotation(
                        tag = DISCLAIMER_TAG,
                        annotation = "",
                        start = startIndex,
                        end = endIndex
                    )
                }
                ClickableText(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = annotatedString,
                    onClick = { offset ->
                        annotatedString
                            .getStringAnnotations(DISCLAIMER_TAG, offset, offset)
                            .firstOrNull()?.let {
                                onDisclaimerClick()
                            }
                    }
                )
            } else {
                Text(
                    modifier = Modifier,
                    text = stringResource(id = questionTextResource),
                    style = Typography.body1,
                    color = labelColor,
                    textAlign = TextAlign.Start
                )
            }
            Spacer(modifier = Modifier.height(26.dp))
            RadioButton(modifier = Modifier.fillMaxWidth(), radioModifier = Modifier.size(20.dp), text = stringResource(id = firstButtonTextResource), selected = firstButtonIsSelected, onOptionSelected = onFirstButtonOnClick)
            Spacer(modifier = Modifier.height(25.dp))
            RadioButton(modifier = Modifier.fillMaxWidth(), radioModifier = Modifier.size(20.dp), text = stringResource(id = secondButtonTextResource), selected = secondButtonIsSelected, onOptionSelected = onSecondButtonOnClick)
            Spacer(modifier = Modifier.height(26.dp))
        }
    }
}

@Composable
fun RadioButton(
    modifier: Modifier,
    radioModifier: Modifier,
    text: String,
    onOptionSelected: () -> Unit = {},
    selected: Boolean
) {
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        var radioSelectedColor = Primary500
        var radioUnSelectedColor = GrayScale500
        var textColor = GrayScale800
        if (isSystemInDarkTheme()) {
            radioSelectedColor = Primary500
            radioUnSelectedColor = GrayScale500
            textColor = WhiteTransparency70
        }
        RadioButton(
            colors = RadioButtonDefaults.colors(radioSelectedColor, radioUnSelectedColor),
            selected = selected,
            modifier = radioModifier,
            onClick = {
                onOptionSelected()
            }
        )
        Spacer(modifier = Modifier.width(18.dp))
        Text(
            modifier = Modifier.padding(top = 2.dp),
            text = text,
            style = Typography.subtitle2,
            color = textColor
        )
    }
}

private const val DISCLAIMER_TAG = "disclaimer"
