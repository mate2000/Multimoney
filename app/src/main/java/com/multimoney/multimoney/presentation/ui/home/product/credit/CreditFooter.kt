package com.multimoney.multimoney.presentation.ui.home.product.credit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.multimoney.domain.model.balance.Balance
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIState
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CreditVisa
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType.PrimaryPrimary

@Composable
fun CreditFooter(
    uiState: UIState,
    balance: Balance?,
    onNavigateToDisbursement: () -> Unit,
    onNavigateToVisaActivateScreen: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        if (uiState.canExpandCredit) {
            CustomButton(
                onClick = { onNavigateToDisbursement() },
                text = stringResource(id = string.home_disburse_request_button_text),
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, bottom = 24.dp)
                    .fillMaxWidth()
                    .height(48.dp),
                buttonType = PrimaryPrimary
            )
        }
        CreditVisa(
            uiState = uiState,
            balance = balance,
            onNavigateToVisaActivateScreen = { onNavigateToVisaActivateScreen() }
        )
    }
}
