package com.multimoney.multimoney.presentation.ui.smart.origination.evicertia.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.smart.origination.sign.SmartSignViewModel
import com.multimoney.multimoney.presentation.ui.smart.origination.sign.SmartSignViewModel.UIEvent
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.CustomImage

@Composable
fun EvercitaDenyContratScreen(
    icon: Int? = null,
    title: Int? = null,
    subtitle: Int? = null,
    viewModel: SmartSignViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight(0.80f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomImage(drawableResource = icon ?: R.drawable.ic_alert)
            Text(
                text = stringResource(id = title ?: R.string.smart_evercita_title),
                modifier = Modifier.padding(top = 24.dp),
                style = Typography.h5.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 28.sp
                ),
                color = MultimoneyTheme.colors.text,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(
                    id = subtitle ?: R.string.smart_evercita_subtitle
                ),
                modifier = Modifier.padding(top = 10.dp),
                style = Typography.body1,
                color = MultimoneyTheme.colors.text,
                textAlign = TextAlign.Center
            )
        }
        Row(
            modifier = Modifier
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CustomButton(
                onClick = { viewModel.onUIEvent(UIEvent.OnNavigateToHome) },
                text = stringResource(id = R.string.understood),
                modifier = Modifier
                    .padding(vertical = 40.dp, horizontal = 16.dp)
                    .fillMaxWidth()
                    .height(48.dp),
                buttonType = CustomButtonType.PrimaryPrimary
            )
        }
    }
}
