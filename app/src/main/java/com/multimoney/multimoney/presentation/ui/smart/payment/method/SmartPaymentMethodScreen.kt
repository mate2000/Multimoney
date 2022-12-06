package com.multimoney.multimoney.presentation.ui.smart.payment.method

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.smart.payment.method.SmartPaymentMethodViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.smart.payment.method.SmartPaymentMethodViewModel.UIEvent.OnTransferSelected
import com.multimoney.multimoney.presentation.ui.smart.payment.method.SmartPaymentMethodViewModel.UIEvent.OnVisaSelected
import com.multimoney.multimoney.presentation.uielement.CustomInfoButton
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun SmartPaymentMethodScreen(
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    viewModel: SmartPaymentMethodViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {
        viewModel.executeNavigation(onNavigate = onNavigate, onPopAndNavigate = onPopAndNavigate)
    }

    Column(
        modifier = Modifier
            .background(MultimoneyTheme.colors.background)
            .fillMaxSize()
    ) {
        TopNavBar(
            onLeftButtonClick = { viewModel.onUIEvent(OnNavigateBack) },
            isRightButtonVisible = false
        )
        PaymentOptions({ viewModel.onUIEvent(OnTransferSelected) },
            { viewModel.onUIEvent(OnVisaSelected) }))
    }
}

@Composable
fun PaymentOptions(onTransferClick: () -> Unit, onVisaClick: () -> Unit) {
    Column(Modifier.padding(horizontal = 16.dp)) {
        Text(
            modifier = Modifier.padding(top = 32.dp),
            text = stringResource(R.string.payment_method_title),
            style = Typography.h5.copy(
                fontWeight = FontWeight.SemiBold,
                color = MultimoneyTheme.colors.text
            )
        )
        CustomInfoButton(
            title = stringResource(id = R.string.payment_method_transfer),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            endIcon = R.drawable.ic_right_chevron,
            startIcon = R.drawable.ic_payment_transfer,
            onEndIconClick = {
                onTransferClick()
            }
        )

        CustomInfoButton(
            title = stringResource(id = R.string.payment_method_visa),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            endIcon = R.drawable.ic_right_chevron,
            startIcon = R.drawable.ic_payment_visa,
            onEndIconClick = {
                onVisaClick()
            }
        )
    }
}