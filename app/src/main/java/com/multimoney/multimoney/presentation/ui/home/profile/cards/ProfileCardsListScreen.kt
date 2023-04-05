package com.multimoney.multimoney.presentation.ui.home.profile.cards

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.Brand
import com.multimoney.multimoney.R
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.extension.findActivity
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.home.profile.cards.ProfileCardListViewModel.UIEvent.OnHideToast
import com.multimoney.multimoney.presentation.uielement.CardListDetail
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.CustomImage
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun ProfileCardsListScreen(
    isRestart: Boolean = true,
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: ProfileCardListViewModel = hiltViewModel()
) {
    // Properties
    val activity = LocalContext.current.findActivity()

    // Navigation
    viewModel.apply {
        isOnRestart = isRestart
        LaunchedEffect(isOnRestart) {
            if (isOnRestart) {
                executeNavigation(onNavigate = onNavigate, onPopBackStack = onPopBackStack)
                onUIEvent(ProfileCardListViewModel.UIEvent.OnCallQueryGetClientCards)
                isOnRestart = false
            }
        }
    }

    // View
    if (viewModel.uiState.toastIsVisible) {
        Toast.makeText(activity, viewModel.uiState.toastMessage, Toast.LENGTH_LONG).show()
        viewModel.onUIEvent(OnHideToast)
    }
    ProfileCardsListContent(viewModel)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
@Preview
fun ProfileCardsListContent(
    viewModel: ProfileCardListViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .background(MultimoneyTheme.colors.background)
            .fillMaxSize()
    ) {
        TopNavBar(
            onLeftButtonClick = { viewModel.onUIEvent(ProfileCardListViewModel.UIEvent.OnNavigateBack) },
            isRightButtonVisible = false
        )
        Text(
            modifier = Modifier.padding(top = 24.dp, start = 16.dp, end = 16.dp),
            text = stringResource(id = string.payment_cards_list_title),
            style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.labelText,
            textAlign = TextAlign.Left
        )
        if (viewModel.uiState.isCardListEmpty) {
            ProfileCardListEmptyState(if (viewModel.idBrand == Brand.CostaRica.id) string.profile_cards_empty_state_cr else string.profile_cards_empty_state_sv)
        } else {
            ProfileCardList(viewModel)
        }

        if (viewModel.uiState.openDialog.isActive.value) {
            CustomDialog(
                title = stringResource(id = viewModel.uiState.openDialog.titleResource),
                message = stringResource(id = viewModel.uiState.openDialog.descriptionResource).ifEmpty { viewModel.uiState.openDialog.description },
                positiveButtonText = stringResource(id = viewModel.uiState.openDialog.positiveResource),
                negativeButtonText = stringResource(id = viewModel.uiState.openDialog.negativeResource),
                positiveButtonColor = if (viewModel.uiState.deleteDialogIsVisible) {
                    MultimoneyTheme.colors.dialogNegativeButtonColor
                } else {
                    MultimoneyTheme.colors.dialogPositiveButtonColor
                },
                openDialogCustom = viewModel.uiState.openDialog.isActive,
                onNegativeAction = viewModel.uiState.openDialog.negativeAction,
                onPositiveAction = viewModel.uiState.openDialog.positiveAction
            )
        }
    }
    ProfileCardEditBottomSheet(
        coroutineScope = coroutineScope,
        modalBottomSheetState = viewModel.uiState.bottomSheetVisibleState,
        onEditClick = {
            if (it?.verified == true) {
                viewModel.onUIEvent(ProfileCardListViewModel.UIEvent.OnEditCard(it))
            } else {
                viewModel.onUIEvent(ProfileCardListViewModel.UIEvent.OnNavigateToVerifyCard)
            }
        },
        onDeleteClick = { viewModel.onUIEvent(ProfileCardListViewModel.UIEvent.OnDeleteCard(it)) },
        card = viewModel.uiState.cardVDSelected
    )
    LoadingIndicator(viewModel.uiState.isLoading)
}

@Composable
@Preview
fun ProfileCardListEmptyState(title: Int = string.empty) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomImage(
                drawableResource = R.drawable.ic_visa_cards_empty_state
            )
            Text(
                text = stringResource(id = title),
                modifier = Modifier.padding(vertical = 25.dp, horizontal = 58.dp),
                style = Typography.body1,
                color = MultimoneyTheme.colors.labelText,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
@Preview
fun ProfileCardList(
    viewModel: ProfileCardListViewModel = hiltViewModel()
) {
    viewModel.uiState.cardVDListVerified?.let { cardList ->
        if (cardList.isNotEmpty()) {
            CardListDetail(
                listItems = cardList,
                onEndIconClick = { card ->
                    viewModel.onUIEvent(
                        ProfileCardListViewModel.UIEvent.OnCardThreePointsSelected(
                            card
                        )
                    )
                }
            )
        }
    }
    viewModel.uiState.cardVDListNotVerified?.let { cardList ->
        if (cardList.isNotEmpty()) {
            CardListDetail(
                titleResource = string.payment_cards_not_verified_list_title,
                listItems = cardList,
                onEndIconClick = { card ->
                    viewModel.onUIEvent(
                        ProfileCardListViewModel.UIEvent.OnCardThreePointsSelected(
                            card
                        )
                    )
                },
                requireIcon = true
            )
        }
    }
}
