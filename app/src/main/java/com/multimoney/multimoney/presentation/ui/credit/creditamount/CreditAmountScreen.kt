package com.multimoney.multimoney.presentation.ui.credit.creditamount

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.CreditStep
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.ui.credit.CreditViewModel

@Composable
fun CreditAmountScreen(
    sharedViewModel: CreditViewModel,
    viewModel: CreditAmountViewModel = hiltViewModel()
) {

    LaunchedEffect(key1 = true) {
        sharedViewModel.onUIEvent(CreditViewModel.UIEvent.OnContinueEnable(true))
        sharedViewModel.onUIEvent(CreditViewModel.UIEvent.OnSetNavigation(nextAction = {
            viewModel.onUIEvent(CreditAmountViewModel.UIEvent.OnNextActionClick {
                sharedViewModel.onUIEvent(
                    CreditViewModel.UIEvent.OnNextStep
                )
            })
        }, nextStep = CreditStep.Two.id, previousStep = CreditStep.One.id))
        viewModel.onUIEvent(
            // TODO: Send appropriate data for this call because now we don't have this data
            CreditAmountViewModel.UIEvent.OnCallQueryCreditOfferUseCase(
                onLoadingValueChange = { isLoading ->
                    sharedViewModel.onUIEvent(CreditViewModel.UIEvent.OnLoadingValueChange(isLoading))
                },
                onFailureWithDialog = { isLoading, dialogParameter ->
                    sharedViewModel.onUIEvent(CreditViewModel.UIEvent.OnFailureWithDialog(isLoading, dialogParameter))
                }
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Credit Amount", color = MultimoneyTheme.colors.labelText)
    }
}