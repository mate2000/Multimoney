package com.multimoney.multimoney.presentation.ui.smart.payment.options

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
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.smartaccount.BaseSelectSmartAccountViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.smartaccount.BaseSelectSmartAccountViewModel.UIEvent.OnSmartAccountSelected
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.smartaccount.BaseSelectSmartAccountViewModel
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.CustomInfoButton
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType

@Composable
fun SmartPaymentOptionsScreen(
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: BaseSelectSmartAccountViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {
        viewModel.executeNavigation(onNavigate = onNavigate, onPopBackStack = onPopBackStack)
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
        PaymentOptions(
            onAccountClick = { viewModel.onUIEvent(OnSmartAccountSelected(it)) }
        )
    }

    LoadingIndicator(viewModel.uiState.isLoading)

    if (viewModel.uiState.openDialog.isActive.value) {
        CustomDialog(
            title = stringResource(id = viewModel.uiState.openDialog.titleResource),
            message = stringResource(id = viewModel.uiState.openDialog.descriptionResource).ifEmpty { viewModel.uiState.openDialog.description },
            positiveButtonText = stringResource(id = viewModel.uiState.openDialog.positiveResource),
            openDialogCustom = viewModel.uiState.openDialog.isActive,
            onPositiveAction = viewModel.uiState.openDialog.positiveAction
        )
    }
}

@Composable
fun PaymentOptions(
    onAccountClick: (CurrencyType) -> Unit
) {
    Column(Modifier.padding(horizontal = 16.dp)) {
        Text(
            modifier = Modifier.padding(top = 32.dp),
            text = stringResource(R.string.payment_options_title_cr),
            style = Typography.h5.copy(
                fontWeight = FontWeight.SemiBold,
                color = MultimoneyTheme.colors.text
            )
        )
        CustomInfoButton(
            title = stringResource(id = R.string.payment_account_colon),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            endIcon = R.drawable.ic_right_chevron,
            startIcon = R.drawable.ic_payment_colon,
            onClick = { onAccountClick(CurrencyType.Colon) }
        )

        CustomInfoButton(
            title = stringResource(id = R.string.payment_account_dollar),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            endIcon = R.drawable.ic_right_chevron,
            startIcon = R.drawable.ic_payment_dollar,
            onClick = { onAccountClick(CurrencyType.Dollar) }
        )
    }
}
