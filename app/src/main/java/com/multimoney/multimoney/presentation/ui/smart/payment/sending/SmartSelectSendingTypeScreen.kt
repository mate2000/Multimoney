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
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.smart.payment.sending.SmartSelectSendingTypeViewModel.UIEvent.OnNavigateBack
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
        SendingTypeOptions()
    }
    BackHandler { viewModel.onUIEvent(OnNavigateBack) }
}

@Composable
fun SendingTypeOptions(
    onMyContactsClick: () -> Unit,
    onMySmartAccountClick: () -> Unit,
    onIBANAccountsClick: () -> Unit,
    smartAccountTitle: String,
    smartAccountStartIcon: Int?
) {
    Column(Modifier.padding(horizontal = 16.dp)) {
        Text(
            modifier = Modifier.padding(top = 32.dp),
            text = stringResource(R.string.payment_select_sending_type_title),
            style = Typography.h5.copy(
                fontWeight = FontWeight.SemiBold, color = MultimoneyTheme.colors.text
            )
        )
        CustomInfoButton(
            title = stringResource(R.string.payment_select_sending_type_my_contacts),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            endIcon = R.drawable.ic_right_chevron,
            startIcon = R.drawable.ic_sending_contact,
            onEndIconClick = onMyContactsClick,
            onClick = onMyContactsClick
        )
        CustomInfoButton(
            title = smartAccountTitle,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            endIcon = R.drawable.ic_right_chevron,
            startIcon = smartAccountStartIcon,
            onEndIconClick = onMySmartAccountClick,
            onClick = onMySmartAccountClick
        )
        CustomInfoButton(
            title = stringResource(R.string.payment_select_sending_type_iban_accounts),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            endIcon = R.drawable.ic_right_chevron,
            startIcon = R.drawable.ic_sending_iban_account,
            onEndIconClick = onIBANAccountsClick,
            onClick = onIBANAccountsClick
        )
    }
}