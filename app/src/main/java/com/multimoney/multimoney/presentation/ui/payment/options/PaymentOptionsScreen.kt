package com.multimoney.multimoney.presentation.ui.payment.options

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
import androidx.navigation.NavBackStackEntry
import com.multimoney.domain.model.security.PaymentMethod
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.navgraph.CREDIT_NUMBER
import com.multimoney.multimoney.presentation.navigation.navgraph.PAYMENT_METHOD
import com.multimoney.multimoney.presentation.navigation.navgraph.TRANSFER_ACCOUNT
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.payment.options.PaymentOptionsViewModel.UIEvent.OnGetTextResources
import com.multimoney.multimoney.presentation.ui.payment.options.PaymentOptionsViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.payment.options.PaymentOptionsViewModel.UIEvent.OnSaveArguments
import com.multimoney.multimoney.presentation.uielement.CustomInfoButton
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.getPaymentMethodType

@Composable
fun PaymentOptionsScreen(
    navBackStackEntry: NavBackStackEntry,
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopBackStack: () -> Unit = {},
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    viewModel: PaymentOptionsViewModel = hiltViewModel()
) {
    // Navigation
    LaunchedEffect(true) {
        viewModel.apply {
            executeNavigation(
                onNavigate = onNavigate,
                onPopBackStack = onPopBackStack,
                onPopAndNavigate = onPopAndNavigate
            )
            navBackStackEntry.arguments?.apply {
                viewModel.onUIEvent(
                    OnSaveArguments(
                        getInt(ID_BRAND),
                        getString(CREDIT_NUMBER),
                        (get(PAYMENT_METHOD) as Array<PaymentMethod>).toList(),
                        getParcelable(TRANSFER_ACCOUNT)
                    )
                )
            }
            onUIEvent(OnGetTextResources)
        }
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
            modifier = Modifier.padding(top = 42.dp, start = 16.dp, end = 16.dp),
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
//                            viewModel.onUIEvent(OnClientBankAccountSelected(clientBankAccount))
                        }
                    )
                }
            }
        }
    }
}
