package com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards

import android.widget.Toast
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardListViewModel.UIEvent.OnCallQueryGetCards
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardListViewModel.UIEvent.OnCardSelected
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardListViewModel.UIEvent.OnCloseClick
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardListViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.CustomInfoButton
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun PaymentScheduleCardListScreen(
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: PaymentScheduleCardListViewModel = hiltViewModel()
) {
    // Navigation
    LaunchedEffect(true) {
        viewModel.apply {
            executeNavigation(onPopAndNavigate = onPopAndNavigate, onPopBackStack = onPopBackStack)
            onUIEvent(OnCallQueryGetCards)
        }
    }
    PaymentScheduleAccountContent(viewModel)
}

@Composable
@Preview
fun PaymentScheduleAccountContent(
    viewModel: PaymentScheduleCardListViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier
            .background(MultimoneyTheme.colors.background)
            .fillMaxSize()
    ) {
        TopNavBar(
            onLeftButtonClick = { viewModel.onUIEvent(OnNavigateBack) },
            onRightButtonClick = { viewModel.onUIEvent(OnCloseClick) }
        )
        Text(
            modifier = Modifier.padding(top = 24.dp, start = 16.dp, end = 16.dp),
            text = stringResource(id = R.string.payment_schedule_card_list_title),
            style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.text,
            textAlign = TextAlign.Left
        )
        val context = LocalContext.current

        viewModel.uiState.clientCardList?.let { cardList ->
            LazyColumn(modifier = Modifier.padding(top = 24.dp, start = 16.dp, end = 16.dp)) {
                items(cardList) { card ->
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
                        endIcon = R.drawable.ic_right_chevron,
                        onEndIconClick = {
                            viewModel.onUIEvent(OnCardSelected(card))
                        },
                        onClick = {
                            viewModel.onUIEvent(OnCardSelected(card))
                        }

                    )
                }
            }
        }
        CustomButton(
            text = stringResource(id = R.string.payment_schedule_card_list_add_card),
            modifier = Modifier
                .padding(top = 32.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth(),
            onClick = {
                Toast.makeText(context, "TBD", Toast.LENGTH_SHORT).show()
            },
            buttonType = CustomButtonType.PrimaryTertiary,
            trailingIcon = R.drawable.ic_plus
        )
        if (viewModel.uiState.openDialog.isActive.value) {
            CustomDialog(
                title = stringResource(id = viewModel.uiState.openDialog.titleResource),
                message = stringResource(id = viewModel.uiState.openDialog.descriptionResource).ifEmpty { viewModel.uiState.openDialog.description },
                positiveButtonText = stringResource(id = viewModel.uiState.openDialog.positiveResource),
                negativeButtonText = stringResource(id = viewModel.uiState.openDialog.negativeResource),
                openDialogCustom = viewModel.uiState.openDialog.isActive,
                onPositiveAction = viewModel.uiState.openDialog.positiveAction,
                onNegativeAction = viewModel.uiState.openDialog.negativeAction
            )
        }
    }
    LoadingIndicator(viewModel.uiState.isLoading)
}
