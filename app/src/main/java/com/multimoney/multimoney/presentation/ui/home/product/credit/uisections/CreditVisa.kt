package com.multimoney.multimoney.presentation.ui.home.product.credit.uisections

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.multimoney.data.util.catalog.CreditStatus
import com.multimoney.domain.model.balance.Balance
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIState
import com.multimoney.multimoney.presentation.uielement.BoxVisaType.CreditCard
import com.multimoney.multimoney.presentation.uielement.BoxVisaType.RequestCreditCard
import com.multimoney.multimoney.presentation.uielement.CustomBoxVisaBackground

@Composable
fun CreditVisa(
    uiState: UIState,
    balance: Balance?,
    onNavigateToVisaActivateScreen: () -> Unit
) {
    if (uiState.userStatus?.infoCredit?.status == CreditStatus.EXIST_IN_CORE.status) {
        balance?.balanceCardInformation?.cardInformation?.let { cardInformation ->
            CustomBoxVisaBackground(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                onClick = {
                    onNavigateToVisaActivateScreen()
                },
                type = CreditCard(cardInformation.cardNumber ?: "")
            )
        } ?: run {
            CustomBoxVisaBackground(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                onClick = {
                    onNavigateToVisaActivateScreen()
                },
                type = RequestCreditCard
            )
        }
    }
}
