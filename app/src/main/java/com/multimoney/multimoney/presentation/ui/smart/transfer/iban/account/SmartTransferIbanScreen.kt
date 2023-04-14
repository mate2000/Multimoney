package com.multimoney.multimoney.presentation.ui.smart.transfer.iban.account

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue.Hidden
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R.drawable
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.extension.findActivity
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.account.SmartTransferIbanViewModel.BaseEvent.OnHideAccountOptionsBottomSheet
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.account.SmartTransferIbanViewModel.BaseEvent.OnShowAccountOptionsBottomSheet
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.account.SmartTransferIbanViewModel.UIEvent.OnAccountClick
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.account.SmartTransferIbanViewModel.UIEvent.OnAddAccountClick
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.account.SmartTransferIbanViewModel.UIEvent.OnCallListSinpeAccounts
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.account.SmartTransferIbanViewModel.UIEvent.OnDeleteAccount
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.account.SmartTransferIbanViewModel.UIEvent.OnEditAccount
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.account.SmartTransferIbanViewModel.UIEvent.OnHideToast
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.account.SmartTransferIbanViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.account.SmartTransferIbanViewModel.UIEvent.OnShowAccountOptions
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType.PrimaryTertiary
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.CustomInfoButton
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.getCurrencyFromId
import com.multimoney.multimoney.presentation.util.getMaskedAccountIban
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SmartTransferIbanScreen(
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: SmartTransferIbanViewModel = hiltViewModel()
) {
    // Properties
    val activity = LocalContext.current.findActivity()
    val coroutineScope = rememberCoroutineScope()
    val accountOptionsBottomSheetState = rememberModalBottomSheetState(Hidden)

    LaunchedEffect(true) {
        viewModel.onUIEvent(OnCallListSinpeAccounts)
        viewModel.executeNavigation(onNavigate = onNavigate, onPopBackStack = onPopBackStack)

        viewModel.baseEvent.collect { event ->
            when (event) {
                is OnShowAccountOptionsBottomSheet -> {
                    coroutineScope.launch {
                        accountOptionsBottomSheetState.show()
                    }
                }
                is OnHideAccountOptionsBottomSheet -> {
                    coroutineScope.launch {
                        accountOptionsBottomSheetState.hide()
                    }
                }
            }
        }
    }

    if (viewModel.uiState.toastIsVisible) {
        Toast.makeText(activity, viewModel.uiState.toastMessage, Toast.LENGTH_LONG).show()
        viewModel.onUIEvent(OnHideToast)
    }

    BackHandler {
        viewModel.onUIEvent(OnNavigateBack)
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
        Text(
            modifier = Modifier.padding(top = 16.dp, start = 18.dp, end = 16.dp),
            text = stringResource(string.smart_iban_transfer_accounts_title),
            style = Typography.h5.copy(
                fontWeight = FontWeight.SemiBold,
                color = MultimoneyTheme.colors.text
            )
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = string.smart_iban_transfer_title),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(end = 16.dp),
                style = Typography.subtitle1.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MultimoneyTheme.colors.smartCardTrending
                )
            )
            CustomButton(
                text = stringResource(id = string.smart_iban_transfer_accounts_add),
                onClick = {
                    viewModel.onUIEvent(OnAddAccountClick)
                },
                buttonType = PrimaryTertiary,
                trailingIcon = drawable.ic_plus
            )
        }

        PaymentOptions(viewModel)
    }

    AccountOptionsBottomSheet(
        coroutineScope = coroutineScope,
        modalBottomSheetState = accountOptionsBottomSheetState,
        onEditClick = { viewModel.onUIEvent(OnEditAccount) },
        onDeleteClick = { viewModel.onUIEvent(OnDeleteAccount) }
    )

    LoadingIndicator(viewModel.uiState.isLoading)

    if (viewModel.uiState.openDialog.isActive.value) {
        CustomDialog(
            title = stringResource(id = viewModel.uiState.openDialog.titleResource),
            message = stringResource(id = viewModel.uiState.openDialog.descriptionResource).ifEmpty { viewModel.uiState.openDialog.description },
            negativeButtonText = stringResource(id = viewModel.uiState.openDialog.negativeResource),
            positiveButtonText = stringResource(id = viewModel.uiState.openDialog.positiveResource),
            openDialogCustom = viewModel.uiState.openDialog.isActive,
            onPositiveAction = viewModel.uiState.openDialog.positiveAction
        )
    }
}

@Composable
fun PaymentOptions(viewModel: SmartTransferIbanViewModel = hiltViewModel()) {
    LazyColumn(modifier = Modifier.padding(top = 24.dp, start = 16.dp, end = 16.dp)) {
        items(
            viewModel.uiState.sinpeAccountList.sortedBy { aCHFavoriteAccount ->
                aCHFavoriteAccount?.description.orEmpty()
            }
        ) { account ->
            CustomInfoButton(
                title = account?.description.orEmpty(),
                subtitle = account?.destinationBankDescription.orEmpty(),
                subtitle2 = getMaskedAccountIban(
                    account?.accountNumber.orEmpty()
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(top = 12.dp),
                endIcon = drawable.ic_options,
                onEndIconClick = {
                    viewModel.onUIEvent(OnShowAccountOptions(account))
                },
                startIcon = account?.destinationAccountCurrencyId?.getCurrencyFromId()?.accountIcon,
                onClick = {
                    viewModel.onUIEvent(OnAccountClick(account))
                }
            )
        }
    }
}
