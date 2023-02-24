package com.multimoney.multimoney.presentation.ui.smart.transfer.smart.favorite.account

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.domain.model.accountsmart.ACHAccount
import com.multimoney.multimoney.R
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.favorite.account.SmartTransferFavoriteViewModel.UIEvent.OnCallQueryACHTransferFavoriteListUseCase
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.favorite.account.SmartTransferFavoriteViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.favorite.account.SmartTransferFavoriteViewModel.UIEvent.OnNavigateToHome
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.favorite.account.SmartTransferFavoriteViewModel.UIEvent.OnOptionsClick
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.CustomInfoButton
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.getCurrencyFromId
import com.multimoney.multimoney.presentation.util.getMaskedAccount

@Composable
fun SmartTransferFavoriteScreen(
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: SmartTransferFavoriteViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {
        viewModel.onUIEvent(OnCallQueryACHTransferFavoriteListUseCase)
        viewModel.executeNavigation(onNavigate = onNavigate, onPopBackStack = onPopBackStack)
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
            isRightButtonVisible = true,
            onRightButtonClick = { viewModel.onUIEvent(OnNavigateToHome) },
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

        if (!viewModel.uiState.isLoading) {
            ACHFavoriteContentList(
                AHCFavoriteList = viewModel.uiState.ACHFavoriteAccountList,
                onEndIconClick = { favorite ->
                    viewModel.onUIEvent(OnOptionsClick(favorite))
                },
                onFavoriteClick = { favorite ->
                    viewModel.onUIEvent(
                        SmartTransferFavoriteViewModel.UIEvent.OnFavoriteClick(
                            favorite
                        )
                    )
                }
            )
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
fun ACHFavoriteContentList(
    AHCFavoriteList: Map<String, List<ACHAccount?>>,
    onEndIconClick: (contact: ACHAccount) -> Unit,
    onFavoriteClick: (contact: ACHAccount) -> Unit
) {
    LazyColumn(modifier = Modifier.padding(top = 24.dp, start = 16.dp, end = 16.dp)) {
        AHCFavoriteList.forEach { (_, favorite) ->
            item {
                CustomInfoButton(
                    title = favorite.firstOrNull()?.description.orEmpty(),
                    subtitle = favorite.firstOrNull()?.destinationBankDescription.orEmpty(),
                    subtitle2 = getMaskedAccount(
                        favorite.firstOrNull()?.accountNumber.orEmpty()
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(top = 12.dp),
                    endIcon = R.drawable.ic_options,
                    startIcon = favorite.first()?.destinationAccountCurrencyId?.getCurrencyFromId()?.accountIcon,
                    onEndIconClick = { favorite.firstOrNull()?.let { onEndIconClick(it) } },
                    onClick = {
                        favorite.firstOrNull()?.let { onFavoriteClick(it) }
                    },
                    titleIcon = R.drawable.ic_star_filled
                )
            }
        }
    }
}
