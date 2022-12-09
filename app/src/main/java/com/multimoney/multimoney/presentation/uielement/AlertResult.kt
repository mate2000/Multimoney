package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.uielement.CustomButtonType.PrimaryPrimary
import com.multimoney.multimoney.presentation.uielement.CustomButtonType.PrimaryTertiary

/**
 * CustomDialog: This Dialog is used to match design system
 *
 * Parameters:
 * @param iconResource: Result icon resource.
 * @param titleResource: Result title resource
 * @param descriptionResource: Result description resource
 * @param titleString: Result title string value
 * @param descriptionString: Result description string value
 * @param buttonTextResource: Result button text resource
 * @param buttonTextString: Result button text resource
 * @param isTopNavBarVisible: Make TopNavBar visible
 * @param isLeftButtonVisible: Make left button TopNavBar's visible
 * @param isRightButtonVisible: Make right button TopNavBar's visible
 * @param onLeftButtonClick: Action left button TopNavBar's
 * @param onRightButtonClick: Action right button TopNavBar's
 * @param onButtonClick: Action to execute when button is clicked
 */

@Composable
@Preview
fun AlertResult(
    iconResource: Int = R.drawable.ic_error_symbol,
    titleResource: Int = R.string.empty,
    descriptionResource: Int = R.string.empty,
    titleString: String = "",
    descriptionString: String = "",
    buttonTextResource: Int = R.string.empty,
    isTopNavBarVisible: Boolean = true,
    isLeftButtonVisible: Boolean = true,
    isRightButtonVisible: Boolean = true,
    onLeftButtonClick: () -> Unit = {},
    onRightButtonClick: () -> Unit = {},
    onButtonClick: () -> Unit = {},
    isSecondaryButtonVisible: Boolean = false,
    onSecondaryButtonClick: () -> Unit = {},
    secondaryButtonTextResource: Int = R.string.empty,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.background),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        if (isTopNavBarVisible) {
            TopNavBar(
                isLeftButtonVisible = isLeftButtonVisible,
                isRightButtonVisible = isRightButtonVisible,
                onLeftButtonClick = onLeftButtonClick,
                onRightButtonClick = onRightButtonClick
            )
        }

        Column(
            modifier = Modifier
                .wrapContentHeight().fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomImage(
                drawableResource = iconResource
            )
            Text(
                modifier = Modifier.padding(top = 40.dp, start = 24.dp, end = 24.dp),
                text = if (titleResource != R.string.empty) {
                    stringResource(id = titleResource)
                } else {
                    titleString
                },
                style = Typography.h5.copy(fontWeight = FontWeight.SemiBold),
                color = MultimoneyTheme.colors.labelText,
                textAlign = TextAlign.Center
            )
            Text(
                modifier = Modifier.padding(all = 24.dp),
                text = if (descriptionResource != R.string.empty) {
                    stringResource(id = descriptionResource)
                } else {
                    descriptionString
                },
                style = Typography.body1,
                color = MultimoneyTheme.colors.labelText,
                textAlign = TextAlign.Center
            )
        }
        Column(Modifier.padding(horizontal = 16.dp)) {
            CustomButton(
                modifier = Modifier
                    .fillMaxWidth().height(48.dp),
                onClick = { onButtonClick() },
                text = stringResource(id = buttonTextResource),
                buttonType = PrimaryPrimary
            )
            if (isSecondaryButtonVisible) {
                CustomButton(
                    modifier = Modifier
                        .fillMaxWidth().height(48.dp),
                    onClick = { onSecondaryButtonClick() },
                    text = stringResource(id = secondaryButtonTextResource),
                    buttonType = PrimaryTertiary
                )
            }
            Spacer(Modifier.fillMaxWidth().height(40.dp))
        }
    }
}
