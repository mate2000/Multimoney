package com.multimoney.multimoney.presentation.ui.smart.transfer.smart.favorite.account

import androidx.activity.compose.BackHandler
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
import com.multimoney.domain.model.accountsmart.ACHAccount
import com.multimoney.domain.model.accountsmart.LocalFavorite
import com.multimoney.multimoney.R
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.favorite.account.SmartTransferFavoriteSVViewModel.UIEvent.OnACHFavoriteClick
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.favorite.account.SmartTransferFavoriteSVViewModel.UIEvent.OnCallLocalFavorites
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.favorite.account.SmartTransferFavoriteSVViewModel.UIEvent.OnCallQueryACHTransferFavoriteList
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.favorite.account.SmartTransferFavoriteSVViewModel.UIEvent.OnLocalFavoriteClick
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.favorite.account.SmartTransferFavoriteSVViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.favorite.account.SmartTransferFavoriteSVViewModel.UIEvent.OnNavigateToHome
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.favorite.account.SmartTransferFavoriteSVViewModel.UIEvent.OnOptionsAchClick
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.favorite.account.SmartTransferFavoriteSVViewModel.UIEvent.OnOptionsLocalClick
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
fun SmartTransferFavoriteSVScreen(
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: SmartTransferFavoriteSVViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {
        viewModel.onUIEvent(OnCallQueryACHTransferFavoriteList)
        viewModel.onUIEvent(OnCallLocalFavorites)
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
                aHCFavoriteList = viewModel.uiState.aCHFavoriteAccountList,
                onACHEndIconClick = { favorite ->
                    viewModel.onUIEvent(OnOptionsAchClick(favorite))
                },
                onACHFavoriteClick = { favorite ->
                    viewModel.onUIEvent(OnACHFavoriteClick(favorite))
                },
                localFavoriteList = viewModel.uiState.localFavoriteList,
                onLocalEndIconClick = { favorite ->
                    viewModel.onUIEvent(OnOptionsLocalClick(favorite))
                },
                onLocalFavoriteClick = { favorite ->
                    viewModel.onUIEvent(OnLocalFavoriteClick(favorite))
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
    aHCFavoriteList: List<ACHAccount?>,
    onACHEndIconClick: (contact: ACHAccount) -> Unit,
    onACHFavoriteClick: (contact: ACHAccount) -> Unit,
    localFavoriteList: List<LocalFavorite?>,
    onLocalEndIconClick: (contact: LocalFavorite) -> Unit,
    onLocalFavoriteClick: (contact: LocalFavorite) -> Unit
) {
    LazyColumn(modifier = Modifier.padding(top = 24.dp, start = 16.dp, end = 16.dp)) {
        items(aHCFavoriteList) { achFavorite ->
            CustomInfoButton(
                title = achFavorite?.description.orEmpty(),
                subtitle = achFavorite?.destinationBankDescription.orEmpty(),
                subtitle2 = getMaskedAccount(
                    accountNumber = achFavorite?.accountNumber.orEmpty(),
                    prefix = ""
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(top = 12.dp),
                endIcon = R.drawable.ic_options,
                startIcon = achFavorite?.destinationAccountCurrencyId?.getCurrencyFromId()?.accountIcon,

                onEndIconClick = { achFavorite?.let { onACHEndIconClick(it) } },
                onClick = {
                    achFavorite?.let { onACHFavoriteClick(it) }
                },
                titleIcon = R.drawable.ic_star_filled
            )
        }
        items(localFavoriteList) { localFavorite ->
            CustomInfoButton(
                title = localFavorite?.accountName.orEmpty(),
                subtitle = if (localFavorite?.phoneNumber.isNullOrEmpty().not()) {
                    formatStringPhoneNumber(
                        localFavorite?.phoneNumber.orEmpty(),
                        localFavorite?.areaCode.orEmpty()
                    )
                } else {
                    stringResource(string.sac_account)
                }.plus(SEPARATOR).plus(localFavorite?.currencyAccount),
                subtitle2 = getMaskedAccount(
                    accountNumber = localFavorite?.accountNumber.orEmpty(),
                    prefix = ""
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(top = 12.dp),
                endIcon = R.drawable.ic_options,
                startIcon = localFavorite?.idCurrencyAccount?.getCurrencyFromId()?.accountIcon,
                titleIcon = R.drawable.ic_star_filled,
                onEndIconClick = { localFavorite?.let { onLocalEndIconClick(it) } },
                onClick = { localFavorite?.let { onLocalFavoriteClick(it) } }
            )
        }
    }
}
