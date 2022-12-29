package com.multimoney.multimoney.presentation.ui.smart.payment.sending

import androidx.activity.compose.BackHandler
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
import com.multimoney.data.util.catalog.Brand
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.smart.payment.sending.SmartSelectSendingTypeViewModel.UIEvent.OnIBANAccountSelected
import com.multimoney.multimoney.presentation.ui.smart.payment.sending.SmartSelectSendingTypeViewModel.UIEvent.OnMyContactsSelected
import com.multimoney.multimoney.presentation.ui.smart.payment.sending.SmartSelectSendingTypeViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.smart.payment.sending.SmartSelectSendingTypeViewModel.UIEvent.OnSmartAccountSelected
import com.multimoney.multimoney.presentation.uielement.CustomInfoButton
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun SmartSelectSendingTypeScreen(
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: SmartSelectSendingTypeViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {
        viewModel.apply {
            executeNavigation(onNavigate = onNavigate, onPopBackStack = onPopBackStack)
        }
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
        SendingTypeOptions(
            uiState = viewModel.uiState,
            onMyContactsClick = { viewModel.onUIEvent(OnMyContactsSelected)},
            onMySmartAccountClick = { viewModel.onUIEvent(OnSmartAccountSelected)},
            onIBANAccountsClick = { viewModel.onUIEvent(OnIBANAccountSelected)},
            smartAccountTitle = viewModel.getTitleSmartAccountResource(),
            smartAccountStartIcon = viewModel.getIconSmartAccountResource()
        )
    }
    BackHandler { viewModel.onUIEvent(OnNavigateBack) }
}

@Composable
fun SendingTypeOptions(
    uiState: SmartSelectSendingTypeViewModel.UIState,
    onMyContactsClick: () -> Unit,
    onMySmartAccountClick: () -> Unit,
    onIBANAccountsClick: () -> Unit,
    smartAccountTitle: Int?,
    smartAccountStartIcon: Int?
) {

    // Creating a common modifier for sending options
    val sendingTypeOptionModifier = Modifier
        .fillMaxWidth()
        .padding(top = 12.dp)

    Column(Modifier.padding(horizontal = 16.dp)) {
        Text(
            modifier = Modifier.padding(top = 32.dp),
            text = stringResource(R.string.payment_select_sending_type_title),
            style = Typography.h5.copy(
                fontWeight = FontWeight.SemiBold, color = MultimoneyTheme.colors.text
            )
        )
        uiState.idBrand.apply {
            // Sending option for both SV and CR and title changes depending on idBrand
            CustomInfoButton(
                title = stringResource(
                    when (this) {
                        Brand.ElSalvador.id -> R.string.payment_select_sending_type_favorites_sv
                        Brand.CostaRica.id -> R.string.payment_select_sending_type_favorites_cr
                        else -> R.string.payment_select_sending_type_favorites_cr
                    }
                ),
                modifier = sendingTypeOptionModifier,
                endIcon = R.drawable.ic_right_chevron,
                startIcon = R.drawable.ic_sending_favorites,
                onEndIconClick = onMyContactsClick,
                onClick = onMyContactsClick
            )
            when (this) {
                // These sending options should be available only for CR
                Brand.CostaRica.id -> {
                    CustomInfoButton(
                        title = stringResource(R.string.payment_select_sending_type_my_contacts),
                        modifier = sendingTypeOptionModifier,
                        endIcon = R.drawable.ic_right_chevron,
                        startIcon = R.drawable.ic_sending_contact,
                        onEndIconClick = onMyContactsClick,
                        onClick = onMyContactsClick
                    )
                    smartAccountTitle?.let {
                        CustomInfoButton(
                            title = stringResource(id = it),
                            modifier = sendingTypeOptionModifier,
                            endIcon = R.drawable.ic_right_chevron,
                            startIcon = smartAccountStartIcon,
                            onEndIconClick = onMySmartAccountClick,
                            onClick = onMySmartAccountClick
                        )
                    }
                    CustomInfoButton(
                        title = stringResource(R.string.payment_select_sending_type_iban_accounts),
                        modifier = sendingTypeOptionModifier,
                        endIcon = R.drawable.ic_right_chevron,
                        startIcon = R.drawable.ic_sending_iban_account,
                        onEndIconClick = onIBANAccountsClick,
                        onClick = onIBANAccountsClick
                    )
                }
                // These options should be available only for SV
                Brand.ElSalvador.id -> {
                    CustomInfoButton(
                        title = stringResource(id = R.string.payment_select_sending_type_to_smart_accounts),
                        modifier = sendingTypeOptionModifier,
                        endIcon = R.drawable.ic_right_chevron,
                        startIcon = R.drawable.ic_sending_dollar,
                        onEndIconClick = { },
                        onClick = { }
                    )
                    CustomInfoButton(
                        title = stringResource(id = R.string.payment_select_sending_type_other_bank_accounts),
                        modifier = sendingTypeOptionModifier,
                        endIcon = R.drawable.ic_right_chevron,
                        startIcon = R.drawable.ic_sending_iban_account,
                        onEndIconClick = { },
                        onClick = { }
                    )
                    CustomInfoButton(
                        title = stringResource(id = R.string.payment_select_sending_type_transfer_365_mobile),
                        modifier = sendingTypeOptionModifier,
                        endIcon = R.drawable.ic_right_chevron,
                        startIcon = R.drawable.ic_sending_transfer_365_mobile,
                        onEndIconClick = { },
                        onClick = { }
                    )
                }
            }
        }
    }
}