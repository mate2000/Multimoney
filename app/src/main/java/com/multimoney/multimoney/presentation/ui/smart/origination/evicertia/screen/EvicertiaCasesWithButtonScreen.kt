package com.multimoney.multimoney.presentation.ui.smart.origination.evicertia.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomImage
import com.multimoney.multimoney.presentation.uielement.TopNavBar

@Composable
fun EvicertiaCasesWithButtonScreen(
    icon: Int,
    title: Int,
    subtitle: Int,
    buttonText: Int,
    backAction: () -> Unit?,
    buttonAction: () -> Unit?
) {
    Column(
        modifier = Modifier
            .wrapContentSize()
            .background(MultimoneyTheme.colors.background),
    ) {
        if (backAction() != null) {
            TopNavBar(isLeftButtonVisible = false, onRightButtonClick = { backAction() })
        }

        Column(
            modifier = Modifier.weight(0.85f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomImage(drawableResource = icon)
            Text(
                text = stringResource(id = title),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                style = Typography.h5.copy(fontWeight = FontWeight.SemiBold),
                color = MultimoneyTheme.colors.text,
                textAlign = TextAlign.Center
            )

            Text(
                text = stringResource(id = subtitle),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, top = 14.dp),
                style = Typography.subtitle1,
                color = MultimoneyTheme.colors.text,
                textAlign = TextAlign.Center
            )
        }

        CustomButton(
            text = stringResource(id = buttonText),
            visible = buttonAction() != null,
            onClick = { buttonAction() },
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.15f)
                .padding(end = 16.dp, start = 16.dp, top = 24.dp, bottom = 14.dp)
        )
    }
}