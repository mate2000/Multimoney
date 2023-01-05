package com.multimoney.multimoney.presentation.ui.smart.transfer.sending

import android.Manifest.permission.READ_PHONE_NUMBERS
import android.Manifest.permission.READ_PHONE_STATE
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.Brand
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.smart.transfer.sending.SmartSelectSendingTypeViewModel.UIEvent.OnCloseClick
import com.multimoney.multimoney.presentation.ui.smart.transfer.sending.SmartSelectSendingTypeViewModel.UIEvent.OnIBANAccountSelected
import com.multimoney.multimoney.presentation.ui.smart.transfer.sending.SmartSelectSendingTypeViewModel.UIEvent.OnMyContactsSelected
import com.multimoney.multimoney.presentation.ui.smart.transfer.sending.SmartSelectSendingTypeViewModel.UIEvent.OnMyFavoritesSelected
import com.multimoney.multimoney.presentation.ui.smart.transfer.sending.SmartSelectSendingTypeViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.smart.transfer.sending.SmartSelectSendingTypeViewModel.UIEvent.OnOtherBankAccountsSelected
import com.multimoney.multimoney.presentation.ui.smart.transfer.sending.SmartSelectSendingTypeViewModel.UIEvent.OnTransfer365MobileSelected
import com.multimoney.multimoney.presentation.ui.smart.transfer.sending.SmartSelectSendingTypeViewModel.UIEvent.OnSmartAccountSelected
import com.multimoney.multimoney.presentation.uielement.CustomInfoButton
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun SmartSelectSendingTypeScreen(
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: SmartSelectSendingTypeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission Accepted: Do something

        } else {
            // Permission Denied: Do something
        }
    }

    LaunchedEffect(true) {
        viewModel.apply {
            executeNavigation(onNavigate = onNavigate, onPopBackStack = onPopBackStack)
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.baseEvent.collect { event ->
            when (event) {
                is SmartSelectSendingTypeViewModel.BaseEvent.OnShowTbdToastEvent -> Toast.makeText(
                    context,
                    "TBD",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // Creating a common modifier for sending options
    val sendingTypeOptionModifier = Modifier
        .fillMaxWidth()
        .padding(top = 12.dp)

    Column(
        modifier = Modifier
            .background(MultimoneyTheme.colors.background)
            .fillMaxSize()
    ) {
        TopNavBar(
            isLeftButtonVisible = false,
            onRightButtonClick = { viewModel.onUIEvent(OnCloseClick) }
        )
        SendingTypeOptionsContainer(
            sendingTypeOptions = {
                when (viewModel.idBrand) {
                    // These sending options should be available only for CR
                    Brand.CostaRica.id -> {
                        SendingTypeOptionsCR(
                            modifier = sendingTypeOptionModifier,
                            onMyFavoritesClick = { viewModel.onUIEvent(OnMyFavoritesSelected) },
                            onMyContactsClick = {
                                                launcher.launch(READ_PHONE_NUMBERS)
                                                /*viewModel.onUIEvent(OnMyContactsSelected)*/
                                                },
                            onMySmartAccountClick = { viewModel.onUIEvent(OnSmartAccountSelected) },
                            onIBANAccountsClick = { viewModel.onUIEvent(OnIBANAccountSelected) },
                            smartAccountTitle = viewModel.getTitleSmartAccountResource(),
                            smartAccountStartIcon = viewModel.getIconSmartAccountResource()
                        )
                    }
                    // These options should be available only for SV
                    Brand.ElSalvador.id -> {
                        SendingTypeOptionsSV(
                            modifier = sendingTypeOptionModifier,
                            onMyFavoritesClick = {
                                                 /*viewModel.onUIEvent(OnMyFavoritesSelected)*/
                                launcher.launch(READ_PHONE_NUMBERS)},
                            onMySmartAccountClick = { viewModel.onUIEvent(OnSmartAccountSelected) },
                            onOtherBankAccountsClick = { viewModel.onUIEvent(OnOtherBankAccountsSelected) },
                            onTransfer365MobileClick = { viewModel.onUIEvent(OnTransfer365MobileSelected) }
                        )
                    }
                }
            }
        )
    }
    BackHandler { viewModel.onUIEvent(OnNavigateBack) }
}

@Composable
fun SendingTypeOptionsContainer(
    sendingTypeOptions: @Composable () -> Unit
) {
    Column(Modifier.padding(horizontal = 16.dp)) {
        Text(
            modifier = Modifier.padding(top = 32.dp),
            text = stringResource(R.string.payment_select_sending_type_title),
            style = Typography.h5.copy(
                fontWeight = FontWeight.SemiBold, color = MultimoneyTheme.colors.text
            )
        )
        sendingTypeOptions()
    }
}

@Composable
fun SendingTypeOptionsCR(
    modifier: Modifier,
    onMyFavoritesClick: () -> Unit,
    onMyContactsClick: () -> Unit,
    onMySmartAccountClick: () -> Unit,
    onIBANAccountsClick: () -> Unit,
    smartAccountTitle: Int?,
    smartAccountStartIcon: Int?,
) {
    CustomInfoButton(
        title = stringResource(R.string.payment_select_sending_type_favorites_cr),
        modifier = modifier,
        endIcon = R.drawable.ic_right_chevron,
        startIcon = R.drawable.ic_sending_favorites,
        onEndIconClick = onMyFavoritesClick,
        onClick = onMyFavoritesClick
    )
    CustomInfoButton(
        title = stringResource(R.string.payment_select_sending_type_my_contacts),
        modifier = modifier,
        endIcon = R.drawable.ic_right_chevron,
        startIcon = R.drawable.ic_sending_contact,
        onEndIconClick = onMyContactsClick,
        onClick = onMyContactsClick
    )
    smartAccountTitle?.let {
        CustomInfoButton(
            title = stringResource(id = it),
            modifier = modifier,
            endIcon = R.drawable.ic_right_chevron,
            startIcon = smartAccountStartIcon,
            onEndIconClick = onMySmartAccountClick,
            onClick = onMySmartAccountClick
        )
    }
    CustomInfoButton(
        title = stringResource(R.string.payment_select_sending_type_iban_accounts),
        modifier = modifier,
        endIcon = R.drawable.ic_right_chevron,
        startIcon = R.drawable.ic_sending_iban_account,
        onEndIconClick = onIBANAccountsClick,
        onClick = onIBANAccountsClick
    )
}

@Composable
fun SendingTypeOptionsSV(
    modifier: Modifier,
    onMyFavoritesClick: () -> Unit,
    onMySmartAccountClick: () -> Unit,
    onOtherBankAccountsClick: () -> Unit,
    onTransfer365MobileClick: () -> Unit
) {
    CustomInfoButton(
        title = stringResource(R.string.payment_select_sending_type_favorites_sv),
        modifier = modifier,
        endIcon = R.drawable.ic_right_chevron,
        startIcon = R.drawable.ic_sending_favorites,
        onEndIconClick = onMyFavoritesClick,
        onClick = onMyFavoritesClick
    )
    CustomInfoButton(
        title = stringResource(id = R.string.payment_select_sending_type_to_smart_accounts),
        modifier = modifier,
        endIcon = R.drawable.ic_right_chevron,
        startIcon = R.drawable.ic_sending_dollar,
        onEndIconClick = onMySmartAccountClick,
        onClick = onMySmartAccountClick
    )
    CustomInfoButton(
        title = stringResource(id = R.string.payment_select_sending_type_other_bank_accounts),
        modifier = modifier,
        endIcon = R.drawable.ic_right_chevron,
        startIcon = R.drawable.ic_sending_iban_account,
        onEndIconClick = onOtherBankAccountsClick,
        onClick = onOtherBankAccountsClick
    )
    CustomInfoButton(
        title = stringResource(id = R.string.payment_select_sending_type_transfer_365_mobile),
        modifier = modifier,
        endIcon = R.drawable.ic_right_chevron,
        startIcon = R.drawable.ic_sending_transfer_365_mobile,
        onEndIconClick = onTransfer365MobileClick,
        onClick = onTransfer365MobileClick
    )
}

@Composable
fun permissionLauncher(): ManagedActivityResultLauncher<String, Boolean> {

    return rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {  }
}