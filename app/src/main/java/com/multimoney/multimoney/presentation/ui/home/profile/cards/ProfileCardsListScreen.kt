package com.multimoney.multimoney.presentation.ui.home.profile.cards

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.multimoney.multimoney.R
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.extension.findActivity
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.CustomImage
import com.multimoney.multimoney.presentation.uielement.CustomInfoButton
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.ui.home.profile.cards.ProfileCardListViewModel.UIEvent.OnHideToast

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
            text = stringResource(id = R.string.payment_cards_list_title),
            style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.labelText,
            textAlign = TextAlign.Left
        )
        if (viewModel.uiState.isCardListEmpty) {
            ProfileCardListEmptyState(viewModel)
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
        onEditClick = { viewModel.onUIEvent(ProfileCardListViewModel.UIEvent.OnEditCard(it)) },
        onDeleteClick = { viewModel.onUIEvent(ProfileCardListViewModel.UIEvent.OnDeleteCard(it)) },
        card = viewModel.uiState.cardVDSelected
    )
    LoadingIndicator(viewModel.uiState.isLoading)
}

@Composable
@Preview
fun ProfileCardListEmptyState(
    viewModel: ProfileCardListViewModel = hiltViewModel()
) {
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
                text = stringResource(id = R.string.payment_cards_list_empty_state_description),
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
    viewModel.uiState.cardVDList?.let { clientBankAccountList ->
        LazyColumn(modifier = Modifier.padding(top = 32.dp, start = 16.dp, end = 16.dp)) {
            items(clientBankAccountList) { card ->
                CustomInfoButton(
                    modifier = Modifier
                        .fillMaxWidth(),
                    imageModifier = Modifier.size(48.dp),
                    startIcon = R.drawable.ic_visa_card_item,
                    title = card?.detail ?: "",
                    subtitle = stringResource(
                        id = string.visa_card_masked_number,
                        card?.cardMaskedNumber?.takeLast(4) ?: 0
                    ),
                    endIcon = R.drawable.ic_option_points,
                    onEndIconClick = {
                        viewModel.onUIEvent(ProfileCardListViewModel.UIEvent.OnCardThreePointsSelected(card))
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}
