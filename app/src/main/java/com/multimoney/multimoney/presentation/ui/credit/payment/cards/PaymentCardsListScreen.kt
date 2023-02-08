package com.multimoney.multimoney.presentation.ui.credit.payment.cards

import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.wrapContentSize
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
import com.multimoney.domain.model.virtualcard.CardVisaDirect
import com.multimoney.multimoney.R
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.CustomImage
import com.multimoney.multimoney.presentation.uielement.CustomInfoButton
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PaymentCardsListScreen(
    isRestart: Boolean = true,
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: PaymentCardListViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    // Navigation
    viewModel.apply {
        isOnRestart = isRestart
        LaunchedEffect(isOnRestart) {
            if (isOnRestart) {
                executeNavigation(onNavigate = onNavigate, onPopBackStack = onPopBackStack)
                onUIEvent(PaymentCardListViewModel.UIEvent.OnCallQueryGetClientCards)
                isOnRestart = false
            }
        }
    }
    BackHandler {
        when {
            viewModel.uiState.bottomSheetVisibleState.isVisible -> {
                coroutineScope.launch {
                    viewModel.onUIEvent(PaymentCardListViewModel.UIEvent.OnHidePaymentBottomSheet)
                }
            }
            else -> viewModel.onUIEvent(PaymentCardListViewModel.UIEvent.OnNavigateBack)
        }
    }
    PaymentCardsListContent(viewModel)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
@Preview
fun PaymentCardsListContent(
    viewModel: PaymentCardListViewModel = hiltViewModel(),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) {
    Column(
        modifier = Modifier
            .background(MultimoneyTheme.colors.background)
            .fillMaxSize()
    ) {
        TopNavBar(
            onLeftButtonClick = { viewModel.onUIEvent(PaymentCardListViewModel.UIEvent.OnNavigateBack) },
            onRightButtonClick = { viewModel.onUIEvent(PaymentCardListViewModel.UIEvent.OnNavigateBackHome) }
        )
        Text(
            modifier = Modifier.padding(top = 24.dp, start = 16.dp, end = 16.dp),
            text = stringResource(id = R.string.payment_cards_list_title),
            style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.labelText,
            textAlign = TextAlign.Left
        )
        if (viewModel.uiState.isCardListEmpty) {
            PaymentCardListEmptyState(viewModel)
        } else {
            PaymentCardList(viewModel)
        }

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
    PaymentCardEditBottomSheet(
        coroutineScope = coroutineScope,
        modalBottomSheetState = viewModel.uiState.bottomSheetVisibleState,
        onResumeClick = { viewModel.onUIEvent(PaymentCardListViewModel.UIEvent.OnNavigateBack) },
        card = viewModel.uiState.cardSelected
    )
    LoadingIndicator(viewModel.uiState.isLoading)
}

@Composable
@Preview
fun PaymentCardListEmptyState(
    viewModel: PaymentCardListViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.wrapContentSize())
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
        CustomButton(
            onClick = { Toast.makeText(context, "Soon...", Toast.LENGTH_SHORT).show() },
            text = stringResource(id = R.string.payment_cards_list_create),
            modifier = Modifier
                .padding(vertical = 40.dp, horizontal = 16.dp)
                .fillMaxWidth()
                .height(48.dp),
            buttonType = CustomButtonType.PrimaryPrimary
        )
    }
}

@Composable
@Preview
fun PaymentCardList(
    viewModel: PaymentCardListViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    viewModel.uiState.cardVDListVerified?.let { clientBankAccountList ->
        PaymentCardListDetail(
            titleResource = R.string.payment_cards_verified_list_title,
            listItems = clientBankAccountList,
            onCardClick = { card -> viewModel.onUIEvent(PaymentCardListViewModel.UIEvent.OnCardSelected(card)) }
        )
    }
    viewModel.uiState.cardVDListNotVerified?.let { clientBankAccountList ->
        PaymentCardListDetail(
            titleResource = R.string.payment_cards_not_verified_list_title,
            listItems = clientBankAccountList,
            onCardClick = { card -> viewModel.onUIEvent(PaymentCardListViewModel.UIEvent.OnCardSelected(card)) },
            requireIcon = true
        )
    }
    CustomButton(
        text = stringResource(id = R.string.payment_cards_list_create),
        modifier = Modifier
            .padding(top = 28.dp)
            .fillMaxWidth(),
        onClick = {
            Toast.makeText(context, "TBD", Toast.LENGTH_SHORT).show()
        },
        buttonType = CustomButtonType.PrimaryTertiary,
        trailingIcon = R.drawable.ic_plus
    )
}

@Composable
@Preview
fun PaymentCardListDetail(
    titleResource: Int = string.empty,
    listItems: List<CardVisaDirect?> = listOf(),
    onCardClick: (CardVisaDirect?) -> Unit = {},
    requireIcon: Boolean = false
) {
    Text(
        modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
        text = stringResource(id = titleResource),
        style = Typography.subtitle1.copy(fontWeight = FontWeight.SemiBold),
        color = MultimoneyTheme.colors.subTitleText,
        textAlign = TextAlign.Left
    )
    LazyColumn(modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)) {
        items(listItems) { card ->
            CustomInfoButton(
                modifier = Modifier
                    .fillMaxWidth(),
                imageModifier = Modifier.size(48.dp),
                startIcon = R.drawable.ic_visa_card_item,
                title = card?.detail ?: "",
                titleIcon = if (requireIcon) R.drawable.ic_green_warning else null,
                subtitle = stringResource(
                    id = string.visa_card_masked_number,
                    card?.cardMaskedNumber?.takeLast(4) ?: 0
                ),
                onClick = {
                    onCardClick(card)
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
