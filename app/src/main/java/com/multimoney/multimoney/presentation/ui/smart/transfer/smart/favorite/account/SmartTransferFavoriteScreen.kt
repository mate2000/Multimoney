package com.multimoney.multimoney.presentation.ui.smart.transfer.smart.favorite.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R.drawable
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.favorite.account.SmartTransferFavoriteViewModel.UIEvent.OnAccountClick
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.favorite.account.SmartTransferFavoriteViewModel.UIEvent.OnCallQueryListSinpeAccountUseCaseImpl
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.favorite.account.SmartTransferFavoriteViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.uielement.CustomInfoButton
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.getCurrencyFromId
import com.multimoney.multimoney.presentation.util.getMaskedAccountIban

@Composable
fun SmartTransferFavoriteScreen(
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: SmartTransferFavoriteViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {
        viewModel.onUIEvent(OnCallQueryListSinpeAccountUseCaseImpl)
        viewModel.executeNavigation(onNavigate = onNavigate, onPopBackStack = onPopBackStack)
    }

    Column(
        modifier = Modifier
            .background(MultimoneyTheme.colors.background)
            .fillMaxSize()
    ) {
        TopNavBar(
            isRightButtonVisible = true,
            onRightButtonClick = { },
            onLeftButtonClick = { viewModel.onUIEvent(OnNavigateBack) }
        )
        Text(
            modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
            text = stringResource(string.smart_favorite_transfer_accounts_title),
            style = Typography.h5.copy(
                fontWeight = FontWeight.SemiBold,
                color = MultimoneyTheme.colors.text
            )
        )

        PaymentOptions(viewModel)
    }

    LoadingIndicator(viewModel.uiState.isLoading)

//    if (viewModel.uiState.openDialog.isActive.value) {
//        CustomDialog(
//            title = stringResource(id = viewModel.uiState.openDialog.titleResource),
//            message = stringResource(id = viewModel.uiState.openDialog.descriptionResource).ifEmpty { viewModel.uiState.openDialog.description },
//            positiveButtonText = stringResource(id = viewModel.uiState.openDialog.positiveResource),
//            openDialogCustom = viewModel.uiState.openDialog.isActive,
//            onPositiveAction = viewModel.uiState.openDialog.positiveAction
//        )
//    }
}

@Composable
fun PaymentOptions(viewModel: SmartTransferFavoriteViewModel = hiltViewModel()) {
    LazyColumn(modifier = Modifier.padding(top = 24.dp, start = 16.dp, end = 16.dp)) {
        items(viewModel.uiState.sinpeAccountList) { account ->
            CustomInfoButton(
                title = account?.nameAccount ?: "",
                subtitle = account?.bank ?: "",
                subtitle2 = getMaskedAccountIban(
                    account?.sinpeAccount ?: "",
                    stringResource(id = string.payment_account_masked_text)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(top = 12.dp),
                endIcon = drawable.ic_options,
                startIcon = account?.currencyId?.getCurrencyFromId()?.accountIcon,
                onClick = {
                    viewModel.onUIEvent(OnAccountClick(account))
                }
            )
        }
    }
}