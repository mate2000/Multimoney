package com.multimoney.multimoney.presentation.ui.smart.transfer.transfer365.accountlist

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
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.Brand
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.smart.transfer.transfer365.accountlist.Transfer365AccountListViewModel.UIEvent.OnAccountClick
import com.multimoney.multimoney.presentation.ui.smart.transfer.transfer365.accountlist.Transfer365AccountListViewModel.UIEvent.OnAddAccountClick
import com.multimoney.multimoney.presentation.ui.smart.transfer.transfer365.accountlist.Transfer365AccountListViewModel.UIEvent.OnGetAccountList
import com.multimoney.multimoney.presentation.ui.smart.transfer.transfer365.accountlist.Transfer365AccountListViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.CustomInfoButton
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.getCurrencyFromId
import com.multimoney.multimoney.presentation.util.getMaskedAccount

@Composable
fun Transfer365AccountListScreen(
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: Transfer365AccountListViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {
        viewModel.executeNavigation(
            onNavigate = onNavigate,
            onPopBackStack = onPopBackStack,
            onPopAndNavigate = onPopAndNavigate
        )
        viewModel.onUIEvent(OnGetAccountList)
    }

    Column(
        modifier = Modifier
            .background(MultimoneyTheme.colors.background)
            .fillMaxSize()
    ) {
        if (viewModel.uiState.accounts.isEmpty().not()) {
            TopNavBar(
                onLeftButtonClick = { viewModel.onUIEvent(OnNavigateBack) },
                isRightButtonVisible = false
            )
            Text(
                modifier = Modifier.padding(top = 32.dp, start = 16.dp, end = 16.dp),
                text = stringResource(R.string.transfer_365_account_list_title),
                style = Typography.h6.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MultimoneyTheme.colors.titleText
                )
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.padding(end = 16.dp),
                    text = stringResource(R.string.transfer_365_account_list_subtitle),
                    style = Typography.subtitle1.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MultimoneyTheme.colors.subTitleText
                    )
                )
                CustomButton(
                    text = stringResource(id = R.string.smart_iban_transfer_accounts_add),
                    onClick = {
                        viewModel.onUIEvent(OnAddAccountClick)
                    },
                    buttonType = CustomButtonType.PrimaryTertiary,
                    trailingIcon = R.drawable.ic_plus
                )
            }

            AccountList(viewModel)
        }
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
fun AccountList(viewModel: Transfer365AccountListViewModel = hiltViewModel()) {
    LazyColumn(modifier = Modifier.padding(top = 24.dp, start = 16.dp, end = 16.dp)) {
        items(viewModel.uiState.accounts) { account ->
            CustomInfoButton(
                title = account?.description ?: "",
                subtitle = account?.destinationBankDescription ?: "",
                subtitle2 = getMaskedAccount(
                    prefix = Brand.ElSalvador.countryCode.uppercase(),
                    accountNumber = account?.accountNumber ?: ""
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(top = 12.dp),
                endIcon = R.drawable.ic_options,
                startIcon = account?.destinationAccountCurrencyId?.getCurrencyFromId()?.accountIcon,
                onClick = {
                    viewModel.onUIEvent(OnAccountClick(account))
                }
            )
        }
    }
}
