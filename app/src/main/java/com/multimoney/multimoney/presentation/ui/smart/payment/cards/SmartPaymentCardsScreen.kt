package com.multimoney.multimoney.presentation.ui.smart.payment.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.smart.payment.cards.SmartPaymentCardsViewModel.UIEvent.OnAddCard
import com.multimoney.multimoney.presentation.ui.smart.payment.cards.SmartPaymentCardsViewModel.UIEvent.OnCallQueryGetClientCards
import com.multimoney.multimoney.presentation.ui.smart.payment.cards.SmartPaymentCardsViewModel.UIEvent.OnCardSelected
import com.multimoney.multimoney.presentation.ui.smart.payment.cards.SmartPaymentCardsViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType.PrimaryTertiary
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.CustomInfoButton
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun SmartPaymentCardsScreen(
    isRestart: Boolean = true,
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: SmartPaymentCardsViewModel = hiltViewModel()
) {
    // Navigation
    viewModel.apply {
        isOnRestart = isRestart
        DisposableEffect(isOnRestart) {
            if (isOnRestart) {
                executeNavigation(onNavigate = onNavigate, onPopBackStack = onPopBackStack)
                onUIEvent(OnCallQueryGetClientCards)
            }
            onDispose {
                isOnRestart = false
            }
        }
    }
    PaymentCardsListContent(viewModel)
}

@Composable
fun PaymentCardsListContent(
    viewModel: SmartPaymentCardsViewModel
) {
    Column(
        modifier = Modifier
            .background(MultimoneyTheme.colors.background)
            .fillMaxSize()
    ) {
        TopNavBar(
            onLeftButtonClick = { viewModel.onUIEvent(OnNavigateBack) },
            isRightButtonVisible = false
        )
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
    LoadingIndicator(viewModel.uiState.isLoading)

    Column {
        Text(
            modifier = Modifier.padding(top = 42.dp, start = 16.dp, end = 16.dp, bottom = 20.dp),
            text = stringResource(R.string.smart_payment_cards_list_card_title),
            style = Typography.h6.copy(
                fontWeight = FontWeight.SemiBold,
                color = MultimoneyTheme.colors.text
            )
        )

        PaymentCardList(viewModel)
        CustomButton(
            text = stringResource(id = R.string.payment_cards_list_create),
            modifier = Modifier
                .padding(top = 32.dp)
                .fillMaxWidth(),
            onClick = { viewModel.onUIEvent(OnAddCard) },
            buttonType = PrimaryTertiary,
            trailingIcon = R.drawable.ic_plus
        )
    }
}

@Composable
fun PaymentCardList(
    viewModel: SmartPaymentCardsViewModel
) {
    LazyColumn(modifier = Modifier.padding(top = 32.dp, start = 16.dp, end = 16.dp)) {
        items(viewModel.uiState.cardVDList) { card ->
            card?.let {
                CustomInfoButton(
                    modifier = Modifier
                        .fillMaxWidth(),
                    imageModifier = Modifier.size(48.dp),
                    startIcon = R.drawable.ic_visa_card_item,
                    title = card.detail ?: "",
                    subtitle = card.cardMaskedNumber ?: "",
                    onClick = {
                        viewModel.onUIEvent(OnCardSelected(card))
                    }
                )
            }
        }
    }
}
