package com.multimoney.multimoney.presentation.ui.credit.payment.cards.amount

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.credit.payment.cards.amount.PaymentAmountCardViewModel.UIEvent.OnAutomaticProgrammedPaymentCheckedChanged
import com.multimoney.multimoney.presentation.ui.credit.payment.cards.amount.PaymentAmountCardViewModel.UIEvent.OnPayClick
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.CustomCheckBox
import com.multimoney.multimoney.presentation.uielement.CustomInfoButton
import com.multimoney.multimoney.presentation.uielement.CustomModalBottomSheet
import com.multimoney.multimoney.presentation.util.getMaskedVisa
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PaymentAmountCardBottomSheetScreen(
    viewModel: PaymentAmountCardViewModel,
    coroutineScope: CoroutineScope,
    modalBottomSheetState: ModalBottomSheetState
) {
    CustomModalBottomSheet(
        title = R.string.payment_amount_card_bottom_sheet_title,
        closeIcon = R.drawable.ic_close_bottom_sheet,
        modalBottomSheetState = modalBottomSheetState,
        coroutineScope = coroutineScope
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = viewModel.getCurrentAmountFormatted(),
                style = Typography.h4.copy(fontWeight = FontWeight.W600),
                color = MultimoneyTheme.colors.text,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = stringResource(id = R.string.payment_amount_card_bottom_sheet_from_card),
                style = Typography.body2.copy(fontWeight = FontWeight.W600),
                color = MultimoneyTheme.colors.text,
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.height(4.dp))
            CustomInfoButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp),
                startIcon = R.drawable.ic_visa_card_item,
                title = viewModel.uiState.card?.detail.orEmpty(),
                subtitle = getMaskedVisa(
                    viewModel.uiState.card?.cardMaskedNumber.orEmpty(),
                    stringResource(id = R.string.visa_card_masked_number)
                ),
                endIcon = null,
                enable = false
            )
            Spacer(modifier = Modifier.height(32.dp))
            CustomCheckBox(
                checked = viewModel.uiState.isAutomaticProgrammedPaymentChecked,
                onCheckedChange = { viewModel.onUIEvent(OnAutomaticProgrammedPaymentCheckedChanged(it)) },
                text = stringResource(id = R.string.payment_amount_card_bottom_sheet_amount_automatic_checkbox)
            )

            Spacer(modifier = Modifier.height(40.dp))
            CustomButton(
                modifier = Modifier
                    .height(48.dp)
                    .fillMaxWidth(),
                onClick = {
                    viewModel.onUIEvent(OnPayClick)
                },
                text = stringResource(id = R.string.payment_amount_card_bottom_sheet_button),
                buttonType = CustomButtonType.PrimaryPrimary,
                enable = viewModel.uiState.isLoading.not()
            )
        }
    }
}
