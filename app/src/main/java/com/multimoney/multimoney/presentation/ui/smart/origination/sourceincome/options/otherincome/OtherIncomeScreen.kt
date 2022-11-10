package com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.otherincome

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.SmartSteps
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnCallMutationUpdateGlobalRequestUseCase
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnContinueEnable
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnContinueVisible
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnSetNavigation
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.SourceIncomeViewModel
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.otherincome.OtherIncomeViewModel.BaseEvent.OnFormValidateCompleted
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.otherincome.OtherIncomeViewModel.UIEvent.OnIncomeAmountChange
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.otherincome.OtherIncomeViewModel.UIEvent.OnIncomeSourceChange
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.util.catalog.SourceIncomeOptionType
import com.multimoney.multimoney.presentation.util.getCurrencySymbol
import com.multimoney.multimoney.presentation.util.transformation.formatDecimalMoney

@Preview
@Composable
fun OtherIncomeScreen(
    viewModel: OtherIncomeViewModel = hiltViewModel(),
    sharedViewModel: SmartViewModel = hiltViewModel(),
    sourceIncomeSharedViewModel: SourceIncomeViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = true) {
        sharedViewModel.onUIEvent(OnContinueVisible(true))
        sharedViewModel.onUIEvent(OnContinueEnable(viewModel.isFormValid()))

        sharedViewModel.onUIEvent(
            OnSetNavigation(
                nextAction = {
                    sharedViewModel.onUIEvent(
                        OnCallMutationUpdateGlobalRequestUseCase(
                            accountSmartData = sharedViewModel.accountSmartData?.copy(
                                idEconomicActivity = if (sharedViewModel.idBrandAsInt == Brand.CostaRica.id) {
                                    SourceIncomeOptionType.OtherCR.id.toLong()
                                } else SourceIncomeOptionType.OtherSV.id.toLong(),
                                income = viewModel.uiState.incomeAmount.toFloat(),
                                specifiesIncomeSource = viewModel.uiState.incomeSource,
                                currentStep = SmartSteps.Search.getNameById(sharedViewModel.uiState.currentStep)
                            )
                        )
                    )
                },
                overridePreviousAction = { sourceIncomeSharedViewModel.goBackToMainOptions() },
                nextStep = SmartSteps.Four.id,
                previousStep = SmartSteps.Two.id
            )
        )
        viewModel.baseEvent.collect { event ->
            when (event) {
                is OnFormValidateCompleted -> sharedViewModel.onUIEvent(
                    OnContinueEnable(event.isFormValid)
                )
            }
        }
    }

    OtherIncomeContent(viewModel, sharedViewModel.idBrandAsInt)

    BackHandler {
        sourceIncomeSharedViewModel.onUIEvent(
            SourceIncomeViewModel.UIEvent.OnNavigateToSelectedSourceOfIncomeOption(
                SourceIncomeOptionType.MainSourceIncomeScreenType.id
            )
        )
    }
}

@Composable
fun OtherIncomeContent(viewModel: OtherIncomeViewModel, idBrand: Int) {
    val focusManager = LocalFocusManager.current
    val currencySymbol = stringResource(idBrand.getCurrencySymbol())
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.smart_other_title),
            style = Typography.h6.copy(
                fontWeight = FontWeight.SemiBold,
                color = MultimoneyTheme.colors.text
            )
        )
        CustomOutlinedTextField(
            value = viewModel.uiState.incomeSource,
            onValueChange = {
                viewModel.onUIEvent(OnIncomeSourceChange(it))
            },
            labelText = stringResource(R.string.smart_other_source_of_income_label),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            }),
            isTextArea = true,
            isError = viewModel.uiState.sourceError.first,
            errorMessage = stringResource(viewModel.uiState.sourceError.second),
            isRequired = true
        )

        CustomOutlinedTextField(
            value = viewModel.uiState.incomeAmount,
            onValueChange = {
                viewModel.onUIEvent(OnIncomeAmountChange(it))
            },
            labelText = stringResource(R.string.smart_own_business_monthly_income_label),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            }),
            placeHolder = stringResource(R.string.decimal_income_placeholder, currencySymbol),
            leadingIcon = R.drawable.ic_money_gray,
            customTransformation = formatDecimalMoney(currencySymbol),
            isError = viewModel.uiState.amountError.first,
            errorMessage = stringResource(viewModel.uiState.amountError.second)
        )
    }
}
