package com.multimoney.multimoney.presentation.ui.credit.payment.options

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.credit.payment.options.PaymentOptionsViewModel.UIEvent.OnGetTextResources
import com.multimoney.multimoney.presentation.ui.credit.payment.options.PaymentOptionsViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.credit.payment.options.PaymentOptionsViewModel.UIEvent.OnPaymentMethodClick
import com.multimoney.multimoney.presentation.uielement.CustomInfoButton
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent.Navigate
import com.multimoney.multimoney.presentation.util.NavEvent.PopBackStack
import com.multimoney.multimoney.presentation.util.getPaymentMethodType

@Composable
fun PaymentOptionsScreen(
    onNavigate: (Navigate) -> Unit = {},
    onPopBackStack: (PopBackStack) -> Unit = {},
    viewModel: PaymentOptionsViewModel = hiltViewModel()
) {
    // Navigation
    LaunchedEffect(true) {
        viewModel.apply {
            executeNavigation(
                onNavigate = onNavigate,
                onPopBackStack = onPopBackStack
            )
            onUIEvent(OnGetTextResources)
        }
    }
    PaymentOptionsContent(viewModel)
    BackHandler {
        viewModel.onUIEvent(OnNavigateBack)
    }
}

@Composable
@Preview
fun PaymentOptionsContent(viewModel: PaymentOptionsViewModel = hiltViewModel()) {
    Column(
        modifier = Modifier
            .background(MultimoneyTheme.colors.background)
            .fillMaxSize()
    ) {
        TopNavBar(
            onLeftButtonClick = { viewModel.onUIEvent(OnNavigateBack) },
            onRightButtonClick = { viewModel.onUIEvent(OnNavigateBack) }
        )
        Text(
            modifier = Modifier.padding(top = 24.dp, start = 16.dp, end = 16.dp),
            text = stringResource(id = viewModel.uiState.titleResource),
            style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.labelText,
            textAlign = TextAlign.Left
        )
        viewModel.uiState.paymentMethodList?.let { paymentMethodList ->
            LazyColumn(modifier = Modifier.padding(top = 32.dp, start = 16.dp, end = 16.dp)) {
                items(paymentMethodList) { paymentMethod ->
                    CustomInfoButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        startIcon = paymentMethod.type?.getPaymentMethodType()?.icon ?: 0,
                        title = paymentMethod.description ?: "",
                        onClick = {
                            viewModel.onUIEvent(OnPaymentMethodClick(paymentMethod.type ?: ""))
                        }
                    )
                }
            }
        }
    }
}
