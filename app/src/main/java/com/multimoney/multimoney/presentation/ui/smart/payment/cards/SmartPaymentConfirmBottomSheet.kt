package com.multimoney.multimoney.presentation.ui.smart.payment.cards

import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.smart.payment.cards.SmartPaymentCardsViewModel.UIEvent.OnCallProcessTransferVisaToSmart
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType.PrimaryPrimary
import com.multimoney.multimoney.presentation.uielement.CustomInfoButton
import com.multimoney.multimoney.presentation.uielement.CustomModalBottomSheet
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SmartPaymentConfirmBottomSheet(
    coroutineScope: CoroutineScope,
    modalBottomSheetState: ModalBottomSheetState,
    viewModel: SmartPaymentCardsViewModel
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                text = "$500", // viewModel.Currency + viewModel.amount,
                style = Typography.h4.copy(fontWeight = FontWeight.W600),
                color = MultimoneyTheme.colors.text,
                textAlign = TextAlign.Center
            )
            Text(
                modifier = Modifier.padding(top = 16.dp).fillMaxWidth(),
                text = stringResource(id = R.string.smart_payment_amount_bottom_sheet_from_card),
                style = Typography.body2.copy(fontWeight = FontWeight.W600),
                color = MultimoneyTheme.colors.text,
                textAlign = TextAlign.Start
            )
            CustomInfoButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .padding(top = 4.dp),
                startIcon = R.drawable.ic_visa_card_item,
                title = "Banco example", // viewModel.cardBankName,
                subtitle = stringResource(R.string.visa_card_masked_number, "1234"),
                endIcon = null,
                enable = false
            )
            Icon(
                painter = painterResource(R.drawable.ic_down_arrow_from_to),
                tint = Color.Unspecified,
                contentDescription = "",
                modifier = Modifier.padding(top = 16.dp)
            )
            Text(
                text = stringResource(R.string.smart_payment_amount_bottom_sheet_to),
                style = Typography.body2.copy(fontWeight = FontWeight.W600),
                color = MultimoneyTheme.colors.text,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(top = 8.dp).fillMaxWidth()
            )
            CustomInfoButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .padding(top = 4.dp, bottom = 24.dp),
                startIcon = R.drawable.ic_bank_account_dollar,
                title = stringResource(R.string.smart_payment_amount_bottom_sheet_my_smart_account, "$"),
                endIcon = null,
                enable = false
            )

            CustomButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                onClick = {
                    viewModel.onUIEvent(OnCallProcessTransferVisaToSmart)
                },
                text = stringResource(id = R.string.button_continue),
                buttonType = PrimaryPrimary,
                enable = true,
                visible = true
            )
        }
    }
}
