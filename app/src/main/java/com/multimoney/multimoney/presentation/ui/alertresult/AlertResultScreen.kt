package com.multimoney.multimoney.presentation.ui.alertresult

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import com.multimoney.multimoney.presentation.navigation.ALERT_RESULT_BUTTON_TEXT
import com.multimoney.multimoney.presentation.navigation.ALERT_RESULT_DESCRIPTION
import com.multimoney.multimoney.presentation.navigation.ALERT_RESULT_ICON
import com.multimoney.multimoney.presentation.navigation.ALERT_RESULT_TITLE
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.alertresult.AlertResultViewModel.UIEvent.OnCloseClick
import com.multimoney.multimoney.presentation.uielement.BackCloseNavBar
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType.PrimaryPrimary
import com.multimoney.multimoney.presentation.uielement.CustomImage
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun AlertResultScreen(
    navBackStackEntry: NavBackStackEntry,
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    viewModel: AlertResultViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {
        viewModel.executeNavigation(onPopAndNavigate = onPopAndNavigate)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.background),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        BackCloseNavBar(
            isBackVisible = false,
            onCloseClick = { viewModel.onUIEvent(OnCloseClick) })

        Column(
            modifier = Modifier
                .wrapContentSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomImage(
                drawableResource = navBackStackEntry.arguments?.getInt(ALERT_RESULT_ICON) ?: 0
            )
            Text(
                modifier = Modifier.padding(top = 40.dp, start = 24.dp, end = 24.dp),
                text = stringResource(id = navBackStackEntry.arguments?.getInt(ALERT_RESULT_TITLE) ?: 0),
                style = Typography.h5.copy(fontWeight = FontWeight.SemiBold),
                color = MultimoneyTheme.colors.labelText,
                textAlign = TextAlign.Center
            )
            Text(
                modifier = Modifier.padding(all = 24.dp),
                text = stringResource(id = navBackStackEntry.arguments?.getInt(ALERT_RESULT_DESCRIPTION) ?: 0),
                style = Typography.body1,
                color = MultimoneyTheme.colors.labelText,
                textAlign = TextAlign.Center
            )
        }
        CustomButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 40.dp),
            onClick = { viewModel.onUIEvent(OnCloseClick) },
            text = stringResource(id = navBackStackEntry.arguments?.getInt(ALERT_RESULT_BUTTON_TEXT) ?: 0),
            buttonType = PrimaryPrimary
        )
    }
}