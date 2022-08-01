package com.multimoney.multimoney.presentation.ui.credit.creditamount

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.CreditStep
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.credit.CreditViewModel
import com.multimoney.multimoney.presentation.ui.credit.creditamount.CreditAmountViewModel.Companion.CURRENCY_SEPARATOR
import com.multimoney.multimoney.presentation.ui.credit.creditamount.CreditAmountViewModel.UIEvent.OnCurrencyIndexChanged
import com.multimoney.multimoney.presentation.ui.credit.creditamount.CreditAmountViewModel.UIEvent.OnDisbursementValueChange
import com.multimoney.multimoney.presentation.uielement.CurrencyAmountInput
import com.multimoney.multimoney.presentation.uielement.CustomToggleButton
import com.multimoney.multimoney.presentation.util.transformation.CurrencyIntegerTransformation

@Composable
@Preview
fun CreditAmountScreen(
    sharedViewModel: CreditViewModel = hiltViewModel(),
    viewModel: CreditAmountViewModel = hiltViewModel()
) {

    // Properties
    val focusManager = LocalFocusManager.current

    viewModel.onUIEvent(
        CreditAmountViewModel.UIEvent.OnInitializeErrorMessages(
            minimumDisbursementErrorMessage = R.string.credit_amount_disbursement_minimum_error_message,
            maximumDisbursementErrorMessage = R.string.credit_amount_disbursement_maximum_error_message
        )
    )

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
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 36.dp, bottom = 40.dp),
            text = stringResource(id = R.string.credit_amount_title),
            style = Typography.h5.copy(
                color = MultimoneyTheme.colors.text,
                fontWeight = FontWeight.SemiBold
            )
        )
        CustomToggleButton(
            modifier = Modifier
                .wrapContentSize()
                .align(Alignment.CenterHorizontally),
            selectedIndex = viewModel.uiState.currencyIndex,
            items = viewModel.uiState.currencyItems,
            onIndexChanged = { index -> viewModel.onUIEvent(OnCurrencyIndexChanged(index)) }
        )
        CurrencyAmountInput(
            value = viewModel.uiState.disbursement,
            placeHolder = stringResource(
                id = R.string.credit_amount_disbursement_placeholder,
                viewModel.uiState.currencyItems[viewModel.uiState.currencyIndex]
            ),
            onValueChange = {
                viewModel.onUIEvent(OnDisbursementValueChange(it))
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            }),
            modifier = Modifier.padding(top = 16.dp),
            isRequired = true,
            isRequiredMessage = stringResource(id = R.string.credit_amount_disbursement_minimum_error_message),
            isError = viewModel.uiState.disbursementError.first,
            errorMessage = stringResource(id = viewModel.uiState.disbursementError.second),
            customTransformation = CurrencyIntegerTransformation(
                viewModel.uiState.currencyItems[viewModel.uiState.currencyIndex],
                CURRENCY_SEPARATOR
            ),
            onDebounceValidation = {
                viewModel.onUIEvent(
                    CreditAmountViewModel.UIEvent.OnValidateDisbursement(it)
                )
            }
        )
    }
}