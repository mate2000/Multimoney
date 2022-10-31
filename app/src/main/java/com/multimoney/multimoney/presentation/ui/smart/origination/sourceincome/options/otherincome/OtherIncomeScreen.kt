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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.SmartSteps
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnCallMutationUpdateGlobalRequestUseCase
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnContinueEnable
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnSetNavigation
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.SourceIncomeViewModel
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.otherincome.OtherIncomeViewModel.BaseEvent.OnFormValidateCompleted
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.otherincome.OtherIncomeViewModel.UIEvent
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
        sharedViewModel.onUIEvent(SmartViewModel.UIEvent.OnContinueVisible(true))
        viewModel.baseEvent.collect { event ->
            when (event) {
                is OnFormValidateCompleted -> sharedViewModel.onUIEvent(
                    OnContinueEnable(event.isFormValid)
                )
            }
        }
    }

    LaunchedEffect(key1 = true) {
        sharedViewModel.onUIEvent(
            OnSetNavigation(
                nextAction = {
                    viewModel.onUIEvent(
                        UIEvent.OnNextActionClick(
                            nextStepAction = {
                                sharedViewModel.onUIEvent(
                                    OnCallMutationUpdateGlobalRequestUseCase(
                                        // FIXME, pass whatever needed and obtain it from the uiState variable
                                        accountSmartData = sharedViewModel.accountSmartData?.copy(
                                            status = 1,
                                            currentStep = SmartSteps.Search.getNameById(
                                                sharedViewModel.uiState.currentStep
                                            ),
                                            income = viewModel.uiState.incomeAmount.toFloat(),
                                            specifiesIncomeSource = viewModel.uiState.incomeSource
                                        )
                                    )
                                )
                            }
                        )
                    )
                },
                // TODO check for correct steps
                nextStep = SmartSteps.Five.id,
                previousStep = SmartSteps.Three.id
            )
        )
    }

    OtherIncomeContent(viewModel, sharedViewModel.idBrand)

    BackHandler {
        sourceIncomeSharedViewModel.onUIEvent(
            SourceIncomeViewModel.UIEvent.OnNavigateToSelectedSourceOfIncomeOption(
                SourceIncomeOptionType.MainSourceIncomeScreenType.id
            )
        )
    }
}

@Composable
fun OtherIncomeContent(viewModel: OtherIncomeViewModel, idBrand: String) {
    val focusManager = LocalFocusManager.current
    val currencySymbol = stringResource(idBrand.toInt().getCurrencySymbol())
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
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
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
