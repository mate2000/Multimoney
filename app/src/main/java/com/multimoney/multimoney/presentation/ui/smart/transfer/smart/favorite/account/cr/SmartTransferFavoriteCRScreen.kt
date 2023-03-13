package com.multimoney.multimoney.presentation.ui.smart.transfer.smart.favorite.account.cr

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
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.model.accountsmart.ACHAccount
import com.multimoney.domain.model.accountsmart.LocalSACAccount
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.favorite.account.cr.SmartTransferFavoriteCRViewModel.UIEvent.GetFavoritesLists
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.favorite.account.cr.SmartTransferFavoriteCRViewModel.UIEvent.OnAccountSelected
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.favorite.account.cr.SmartTransferFavoriteCRViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.favorite.account.cr.SmartTransferFavoriteCRViewModel.UIEvent.OnNavigateToHome
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.favorite.account.cr.SmartTransferFavoriteCRViewModel.UIEvent.OnOptionsClick
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.CustomInfoButton
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.SEPARATOR
import com.multimoney.multimoney.presentation.util.formatStringPhoneNumber
import com.multimoney.multimoney.presentation.util.getCurrencyFromId
import com.multimoney.multimoney.presentation.util.getMaskedAccount

@Composable
fun SmartTransferFavoriteCRScreen(
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: SmartTransferFavoriteCRViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {
        viewModel.onUIEvent(GetFavoritesLists)
        viewModel.executeNavigation(onNavigate = onNavigate, onPopBackStack = onPopBackStack)
    }

    Column(
        modifier = Modifier
            .background(MultimoneyTheme.colors.background)
            .fillMaxSize()
    ) {
        TopNavBar(
            onRightButtonClick = { viewModel.onUIEvent(OnNavigateToHome) },
            onLeftButtonClick = { viewModel.onUIEvent(OnNavigateBack) }
        )
        Text(
            modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
            text = stringResource(R.string.smart_favorite_transfer_accounts_title),
            style = Typography.h5.copy(
                fontWeight = FontWeight.SemiBold,
                color = MultimoneyTheme.colors.text
            )
        )

        if (!viewModel.uiState.isLoading) {
            FavoritesContentList(
                localFavoritesList = viewModel.uiState.localFavoriteList,
                aCHFavoriteList = viewModel.uiState.aCHFavoriteAccountList,
                onEndIconClick = { favorite ->
                    viewModel.onUIEvent(OnOptionsClick(favorite))
                },
                onFavoriteClick = { favorite ->
                    viewModel.onUIEvent(OnAccountSelected(favorite))
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
fun FavoritesContentList(
    localFavoritesList: List<LocalSACAccount?>,
    aCHFavoriteList: List<ACHAccount?>,
    onEndIconClick: (account: Any?) -> Unit,
    onFavoriteClick: (account: Any?) -> Unit
) {
    LazyColumn(modifier = Modifier.padding(top = 24.dp, start = 16.dp, end = 16.dp)) {
        localFavoritesList.forEach { favorite ->
            item {
                CustomInfoButton(
                    title = favorite?.accountName.orEmpty(),
                    subtitle = formatStringPhoneNumber(
                        favorite?.phoneNumber.orEmpty(),
                        favorite?.areaCode.orEmpty()
                    ).plus(SEPARATOR).plus(favorite?.currency),
                    subtitle2 = getMaskedAccount(
                        accountNumber = favorite?.accountNumber.orEmpty(),
                        prefix = Brand.CostaRica.iban
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(top = 12.dp),
                    endIcon = R.drawable.ic_options,
                    startIcon = favorite?.idCurrency?.getCurrencyFromId()?.accountIcon,
                    titleIcon = R.drawable.ic_star_filled,
                    onEndIconClick = { favorite?.let { onEndIconClick(it) } },
                    onClick = {
                        onFavoriteClick(favorite)
                    }
                )
            }
        }

        aCHFavoriteList.forEach { favorite ->
            item {
                CustomInfoButton(
                    title = favorite?.description.orEmpty(),
                    subtitle = favorite?.destinationBankDescription.orEmpty(),
                    subtitle2 = getMaskedAccount(
                        favorite?.accountNumber.orEmpty()
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(top = 12.dp),
                    endIcon = R.drawable.ic_options,
                    startIcon = favorite?.destinationAccountCurrencyId?.getCurrencyFromId()?.accountIcon,
                    titleIcon = R.drawable.ic_star_filled,
                    onEndIconClick = { favorite?.let { onEndIconClick(it) } },
                    onClick = {
                        onFavoriteClick(favorite)
                    }
                )
            }
        }
    }
}
