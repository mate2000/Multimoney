package com.multimoney.multimoney.presentation.ui.credit.disbursement.account

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
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.credit.disbursement.account.DisbursementAccountViewModel.UIEvent.OnProcessCreditExtension
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.CustomInfoButton
import com.multimoney.multimoney.presentation.uielement.CustomModalBottomSheet
import com.multimoney.multimoney.presentation.util.getCurrencyFromId
import com.multimoney.multimoney.presentation.util.getMaskedAccount
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DisbursementBottomSheetScreen(
    viewModel: DisbursementAccountViewModel,
    coroutineScope: CoroutineScope,
    modalBottomSheetState: ModalBottomSheetState
) {
    CustomModalBottomSheet(
        title = R.string.disbursement_bottom_sheet_title,
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
                text = if (viewModel.shouldDisplayExchangeRate()) {
                    viewModel.getCurrentAmountExchangedFormatted()
                } else {
                    viewModel.getCurrentAmountFormatted()
                },
                style = Typography.h4.copy(fontWeight = FontWeight.W600),
                color = MultimoneyTheme.colors.text,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = stringResource(id = R.string.disbursement_bottom_sheet_account),
                style = Typography.body2.copy(fontWeight = FontWeight.W600),
                color = MultimoneyTheme.colors.text,
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.height(4.dp))
            CustomInfoButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp),
                startIcon = viewModel.uiState.clientBankAccountSelected?.idCurrency?.getCurrencyFromId()?.accountIcon
                    ?: R.drawable.ic_bank_account,
                title = viewModel.uiState.clientBankAccountSelected?.bankDescription ?: "",
                subtitle = getMaskedAccount(
                    viewModel.uiState.clientBankAccountSelected?.accountNumber ?: "",
                    stringResource(id = R.string.payment_account_masked_text)
                ),
                endIcon = null,
                enable = false
            )
            Spacer(modifier = Modifier.height(60.dp))
            if (viewModel.shouldDisplayExchangeRate()) {
                Text(
                    text = getDetailTextsDisbursement(
                        R.string.disbursement_bottom_sheet_exchange_rate,
                        viewModel.getExchangeRateFormatted()
                    ),
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            Text(
                text = getDetailTextsDisbursement(
                    R.string.disbursement_bottom_sheet_quota_to_pay,
                    viewModel.getQuotaTotalFormatted()
                ),
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = getDetailTextsDisbursement(
                    R.string.disbursement_bottom_sheet_next_payment_date,
                    viewModel.getQuotaNextDateFormatted()
                ),
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.height(40.dp))
            CustomButton(
                modifier = Modifier
                    .height(48.dp)
                    .fillMaxWidth(),
                onClick = {
                    viewModel.onUIEvent(OnProcessCreditExtension)
                },
                text = stringResource(id = R.string.disbursement_bottom_sheet_button),
                buttonType = CustomButtonType.PrimaryPrimary,
                enable = viewModel.uiState.isLoading.not()
            )
        }
    }
}

@Composable
fun getDetailTextsDisbursement(descriptionResource: Int, value: String) =
    buildAnnotatedString {
        withStyle(
            style = Typography.body2.toSpanStyle()
                .copy(
                    color = MultimoneyTheme.colors.quickActionLabelColor
                )
        ) {
            append(
                stringResource(id = descriptionResource)
            )
            append(" ")
        }
        withStyle(
            style = Typography.body2.toSpanStyle()
                .copy(
                    color = MultimoneyTheme.colors.quickActionLabelColor,
                    fontWeight = FontWeight.SemiBold
                )
        ) {
            append(value)
        }
    }
