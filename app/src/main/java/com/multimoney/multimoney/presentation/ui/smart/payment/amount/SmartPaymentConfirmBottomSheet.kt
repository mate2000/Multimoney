package com.multimoney.multimoney.presentation.ui.smart.payment.amount

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.multimoney.data.util.catalog.Brand
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnCallProcessTransfer
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType.PrimaryPrimary
import com.multimoney.multimoney.presentation.uielement.CustomInfoButton
import com.multimoney.multimoney.presentation.uielement.CustomModalBottomSheet
import com.multimoney.multimoney.presentation.uielement.ExchangeTotalLabel
import com.multimoney.multimoney.presentation.util.CARD_NUMBER_LAST_DIGITS
import com.multimoney.multimoney.presentation.util.getMaskedAccountIban
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SmartPaymentConfirmBottomSheet(
    coroutineScope: CoroutineScope,
    modalBottomSheetState: ModalBottomSheetState,
    viewModel: SavingAmountViewModel
) {
    CustomModalBottomSheet(
        title = R.string.smart_payment_amount_bottom_sheet_title,
        closeIcon = R.drawable.ic_close_bottom_sheet,
        modalBottomSheetState = modalBottomSheetState,
        coroutineScope = coroutineScope
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = CenterHorizontally
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                text = viewModel.getFormattedAmount(),
                style = Typography.h4.copy(fontWeight = FontWeight.W600),
                color = MultimoneyTheme.colors.text,
                textAlign = TextAlign.Center
            )
            if (viewModel.editAmountHelper.shouldDisplayExchange) {
                Spacer(modifier = Modifier.height(8.dp))
                ExchangeTotalLabel(
                    totalConverted = viewModel.uiState.convertedAmountLabel
                )
            }
            Text(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth(),
                text = stringResource(viewModel.editAmountHelper.sheetSubtitle),
                style = Typography.body2.copy(fontWeight = FontWeight.W600),
                color = MultimoneyTheme.colors.text,
                textAlign = TextAlign.Start
            )
            CustomInfoButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(78.dp),
                startIcon = viewModel.editAmountHelper.originIcon,
                title = viewModel.editAmountHelper.bankDetail,
                subtitle = if (viewModel.editAmountHelper.idBrand == Brand.CostaRica.id) {
                    getMaskedAccountIban(
                        viewModel.editAmountHelper.maskedCardNumber,
                        stringResource(id = R.string.payment_account_masked_text)
                    )
                } else {
                    stringResource(
                        R.string.visa_card_masked_number,
                        viewModel.editAmountHelper.maskedCardNumber.takeLast(CARD_NUMBER_LAST_DIGITS)
                    )
                },
                endIcon = null,
                enable = false
            )

            Icon(
                painter = painterResource(R.drawable.ic_down_arrow_from_to),
                tint = Color.Unspecified,
                contentDescription = "",
                modifier = Modifier.padding(top = 24.dp)
            )

            Text(
                text = stringResource(R.string.smart_payment_amount_bottom_sheet_to),
                style = Typography.body2.copy(fontWeight = FontWeight.W600),
                color = MultimoneyTheme.colors.text,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
            )
            CustomInfoButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(78.dp),
                startIcon = R.drawable.ic_bank_account_dollar,
                title = stringResource(
                    R.string.smart_payment_amount_bottom_sheet_my_smart_account,
                    viewModel.uiState.currency
                ),
                endIcon = null,
                enable = false
            )

            Spacer(
                Modifier
                    .fillMaxWidth()
                    .height(40.dp)
            )

            CustomButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                onClick = {
                    viewModel.onUIEvent(OnCallProcessTransfer)
                },
                text = stringResource(id = R.string.button_continue),
                buttonType = PrimaryPrimary,
                enable = true,
                visible = true
            )
        }
    }
}
