package com.multimoney.multimoney.presentation.ui.home.product.credit.uisections

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.multimoney.data.util.catalog.CreditStatus
import com.multimoney.domain.model.balance.Balance
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIState
import com.multimoney.multimoney.presentation.uielement.BoxVisaType.CreditCard
import com.multimoney.multimoney.presentation.uielement.BoxVisaType.RequestCreditCard
import com.multimoney.multimoney.presentation.uielement.CustomBoxVisaBackground

@Composable
fun CreditVisa(
    uiState: UIState,
    balance: Balance?,
    onNavigateToVisaActivateScreen: () -> Unit = {},
    onCreateMultimoneyVisa: () -> Unit = {}
) {
    if (uiState.userStatus?.infoCredit?.status == CreditStatus.EXIST_IN_CORE.status && balance?.getFirstSummary()?.applyCommerce == true && balance.getFirstSummary()?.applyCreateCard == true) {
        Divider(color = MultimoneyTheme.colors.dividerWhite30)
        Spacer(modifier = Modifier.height(24.dp))
        balance.balanceCardInformation?.cardInformation?.let { cardInformation ->
            CustomBoxVisaBackground(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                onClick = {
                    onNavigateToVisaActivateScreen()
                },
                type = CreditCard(cardInformation.cardNumber ?: "")
            )
        } ?: run {
            CustomBoxVisaBackground(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                onClick = { onCreateMultimoneyVisa() },
                type = RequestCreditCard
            )
        }
    }
}
