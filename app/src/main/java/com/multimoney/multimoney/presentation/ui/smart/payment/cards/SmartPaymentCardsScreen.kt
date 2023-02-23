package com.multimoney.multimoney.presentation.ui.smart.payment.cards

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.domain.model.virtualcard.CardVisaDirect
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.ReactActivity
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardListViewModel
import com.multimoney.multimoney.presentation.ui.smart.payment.cards.SmartPaymentCardsViewModel.UIEvent.OnAddCard
import com.multimoney.multimoney.presentation.ui.smart.payment.cards.SmartPaymentCardsViewModel.UIEvent.OnCardSelected
import com.multimoney.multimoney.presentation.ui.smart.payment.cards.SmartPaymentCardsViewModel.UIEvent.OnHandleAddCardResponse
import com.multimoney.multimoney.presentation.ui.smart.payment.cards.SmartPaymentCardsViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.smart.payment.cards.SmartPaymentCardsViewModel.UIEvent.OnSetAddCardActivityOnResult
import com.multimoney.multimoney.presentation.ui.smart.payment.cards.SmartPaymentCardsViewModel.UIEvent.OnStart
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

    val context = LocalContext.current

    // Navigation
    viewModel.apply {
        isOnRestart = isRestart
        LaunchedEffect(isOnRestart) {
            executeNavigation(onNavigate = onNavigate, onPopBackStack = onPopBackStack)
            if (isOnRestart) {
                viewModel.onUIEvent(OnStart)
                isOnRestart = false
            }
        }
    }

    val addCardActivityResult = rememberLauncherForActivityResult(
        contract = StartActivityForResult()
    ) {
        when (it.resultCode) {
            PaymentScheduleCardListViewModel.RESULT_CODE_PROCESS_FINISHED -> {
                val response: String? =
                    it.data?.getStringExtra(PaymentScheduleCardListViewModel.RESPONSE_VALUE)
                val isError: Boolean? = it.data?.getBooleanExtra(
                    PaymentScheduleCardListViewModel.RESPONSE_IS_ERROR,
                    false
                )
                viewModel.onUIEvent(
                    OnHandleAddCardResponse(
                        response = response.orEmpty(),
                        isError = isError ?: false
                    )
                )
            }
            PaymentScheduleCardListViewModel.RESULT_CODE_PROCESS_INCOMPLETE -> {
                viewModel.onUIEvent(OnNavigateBack)
            }
            else -> return@rememberLauncherForActivityResult
        }
    }

    viewModel.onUIEvent(OnSetAddCardActivityOnResult {
        val intent = Intent(context, ReactActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        addCardActivityResult.launch(intent)
    })

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
        PaymentCardsListContent(viewModel)
    }
}

@Composable
fun PaymentCardsListContent(
    viewModel: SmartPaymentCardsViewModel
) {
    Column(Modifier.padding(horizontal = 16.dp)) {
        Text(
            modifier = Modifier.padding(top = 32.dp),
            text = stringResource(R.string.smart_payment_cards_list_card_title),
            style = Typography.h5.copy(
                fontWeight = FontWeight.SemiBold,
                color = MultimoneyTheme.colors.text
            )
        )

        PaymentCardList(
            cardList = viewModel.uiState.cardVDList,
            modifier = Modifier.padding(top = 32.dp)
        ) { viewModel.onUIEvent(OnCardSelected(it)) }

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

    if (viewModel.uiState.openDialog.isActive.value) {
        CustomDialog(
            title = stringResource(id = viewModel.uiState.openDialog.titleResource),
            message = stringResource(id = viewModel.uiState.openDialog.descriptionResource).ifEmpty { viewModel.uiState.openDialog.description },
            positiveButtonText = stringResource(id = viewModel.uiState.openDialog.positiveResource),
            openDialogCustom = viewModel.uiState.openDialog.isActive,
            onPositiveAction = viewModel.uiState.openDialog.positiveAction
        )
    }
    LoadingIndicator(viewModel.uiState.isLoading)
}

@Composable
fun PaymentCardList(
    cardList: List<CardVisaDirect?>,
    modifier: Modifier = Modifier,
    onCardSelected: (CardVisaDirect) -> Unit
) {
    LazyColumn(modifier = modifier) {
        items(cardList) { card ->
            card?.let {
                CustomInfoButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    imageModifier = Modifier.size(48.dp),
                    startIcon = R.drawable.ic_visa_card_item,
                    title = card.detail ?: "",
                    subtitle = stringResource(
                        R.string.visa_card_masked_number,
                        card.cardMaskedNumber.orEmpty().takeLast(4)
                    ),
                    onClick = { onCardSelected(card) }
                )
            }
        }
    }
}
