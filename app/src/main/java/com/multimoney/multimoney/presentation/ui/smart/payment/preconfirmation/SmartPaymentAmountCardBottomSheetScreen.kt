package com.multimoney.multimoney.presentation.ui.smart.payment.preconfirmation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.R.drawable
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.smart.payment.send.SmartSendViewModel
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType.PrimaryPrimary
import com.multimoney.multimoney.presentation.uielement.CustomImage
import com.multimoney.multimoney.presentation.uielement.CustomInfoButton
import com.multimoney.multimoney.presentation.uielement.CustomModalBottomSheet
import com.multimoney.multimoney.presentation.util.getMaskedAccountIban
import com.multimoney.multimoney.presentation.util.getMaskedVisa
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SmartPaymentAmountCardBottomSheetScreen(
    coroutineScope: CoroutineScope,
    modalBottomSheetState: ModalBottomSheetState,
    //viewModel: SmartSendViewModel = hiltViewModel()
) {

    // TODO Implement viewmodel values on every field
    CustomModalBottomSheet(
        title = string.smart_payment_sheet_title,
        closeIcon = drawable.ic_close_bottom_sheet,
        modalBottomSheetState = modalBottomSheetState,
        coroutineScope = coroutineScope
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp)
        ) {
            Row(modifier = Modifier.wrapContentSize()) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "viewModel.uiState.amount",
                    style = Typography.h4.copy(fontWeight = FontWeight.W600),
                    color = MultimoneyTheme.colors.text,
                    textAlign = TextAlign.Center
                )
                CustomImage(drawableResource = drawable.ic_transformation)
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = string.smart_payment_sheet_equivalent, "viewModel.uiState.amount"),
                    style = Typography.h4.copy(fontWeight = FontWeight.W600),
                    color = MultimoneyTheme.colors.text,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(id = string.smart_payment_sheet_from_account),
                style = Typography.body2.copy(fontWeight = FontWeight.W600),
                color = MultimoneyTheme.colors.text,
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.height(4.dp))

            CustomInfoButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp),
                startIcon = drawable.ic_multimoney_white_logo,
                title = "viewModel.uiState.selectedAccountTitle",
                subtitle = getMaskedVisa(
                    "312424234234"/*viewModel.uiState.selectedAccountNumber*/,
                    stringResource(id = string.visa_card_masked_number)
                ),
                endIcon = null,
                enable = false
            )

            Spacer(modifier = Modifier.height(32.dp))
            CustomImage(drawableResource = drawable.ic_down_arrow_from_to)
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(id = string.smart_payment_sheet_to_account),
                style = Typography.body2.copy(fontWeight = FontWeight.W600),
                color = MultimoneyTheme.colors.text,
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.height(4.dp))
            CustomInfoButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp),
                startIcon = R.drawable.ic_bank_account_colon/*viewModel.uiState.selectedBankIcon*/,
                title = "viewModel.uiState.selectedAccountTitle",
                subtitle = stringResource(
                    id = string.smart_account_beneficiary_content,
                    "viewModel.uiState.receiverBank",
                    "test value"
                    /*getMaskedAccountIban(
                        viewModel.uiState.receiverAccount,
                        stringResource(id = string.payment_account_masked_text)
                    )*/
                ),
                endIcon = null,
                enable = false
            )
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = stringResource(id = string.motive),
                style = Typography.body2.copy(fontWeight = FontWeight.W600),
                color = MultimoneyTheme.colors.text,
                textAlign = TextAlign.Start
            )
            Text(
                text = stringResource(id = string.smart_payment_sheet_to_account),
                style = Typography.body2.copy(fontWeight = FontWeight.W600),
                color = MultimoneyTheme.colors.text,
                textAlign = TextAlign.Start
            )

            CustomButton(
                modifier = Modifier
                    .height(48.dp)
                    .fillMaxWidth(),
                onClick = {
                    ///viewModel.onUIEvent(OnPayClick)
                },
                text = stringResource(id = string.smart_payment_sheet_transfer_button),
                buttonType = PrimaryPrimary,
                enable = true/*viewModel.uiState.isLoading.not()*/
            )
        }
    }
}