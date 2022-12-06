package com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.independentprofessional

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.SmartSteps
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.SourceIncomeViewModel
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.util.catalog.SourceIncomeOptionType
import com.multimoney.multimoney.presentation.util.getCurrencySymbol
import com.multimoney.multimoney.presentation.util.transformation.formatDecimalMoney

@Composable
fun IndProfessionalScreen(
    viewModel: IndProfessionalViewModel = hiltViewModel(),
    sharedViewModel: SmartViewModel = hiltViewModel(),
    sourceIncomeSharedViewModel: SourceIncomeViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {
        sharedViewModel.onUIEvent(SmartViewModel.UIEvent.OnContinueEnable(viewModel.isFormValid()))
        sharedViewModel.onUIEvent(SmartViewModel.UIEvent.OnContinueVisible(true))

        sharedViewModel.onUIEvent(
            SmartViewModel.UIEvent.OnSetNavigation(
                nextAction = {
                    sharedViewModel.onUIEvent(
                        SmartViewModel.UIEvent.OnCallMutationUpdateGlobalRequestUseCase(
                            accountSmartData = sharedViewModel.accountSmartData?.copy(
                                idEconomicActivity = SourceIncomeOptionType.FreeLancer.id.toLong(),
                                currentStep = SmartSteps.Search.getNameById(sharedViewModel.uiState.currentStep),
                                income = viewModel.uiState.incomeAmount.toFloat(),
                                addressDetail = viewModel.uiState.address
                            )
                        )
                    )
                },
                overridePreviousAction = { sourceIncomeSharedViewModel.goBackToMainOptions() },
                nextStep = sourceIncomeSharedViewModel.getNextStep(sharedViewModel.idBrandAsInt),
                previousStep = sourceIncomeSharedViewModel.getPreviousStep(sharedViewModel.idBrandAsInt)
            )
        )
        viewModel.baseEvent.collect { event ->
            when (event) {
                is IndProfessionalViewModel.BaseEvent.OnFormValidateCompleted -> sharedViewModel.onUIEvent(
                    SmartViewModel.UIEvent.OnContinueEnable(
                        event.isFormValid
                    )
                )
            }
        }
    }

    // return to the main options screen whenever tapping on native back button from the device
    BackHandler {
        sourceIncomeSharedViewModel.onUIEvent(
            SourceIncomeViewModel.UIEvent.OnNavigateToSelectedSourceOfIncomeOption(
                SourceIncomeOptionType.MainSourceIncomeScreenType.id
            )
        )
    }

    val focusManager = LocalFocusManager.current
    val currencySymbol = stringResource(sharedViewModel.idBrandAsInt.getCurrencySymbol())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(R.string.smart_ind_professional_title),
            style = Typography.h6.copy(
                fontWeight = FontWeight.SemiBold,
                color = MultimoneyTheme.colors.text
            )
        )
        CustomOutlinedTextField(
            modifier = Modifier.padding(top = 24.dp),
            value = viewModel.uiState.incomeAmount,
            onValueChange = {
                viewModel.onUIEvent(IndProfessionalViewModel.UIEvent.OnIncomeAmountChange(it))
            },
            labelText = stringResource(R.string.smart_ind_professional_monthly_income_label),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            }),
            placeHolder = stringResource(R.string.smart_decimal_income_placeholder, currencySymbol),
            leadingIcon = R.drawable.ic_money_gray,
            customTransformation = formatDecimalMoney(currencySymbol),
            isRequiredMessage = stringResource(R.string.smart_own_business_monthly_income_required)
        )
        CustomOutlinedTextField(
            modifier = Modifier.padding(top = 16.dp),
            value = viewModel.uiState.address,
            onValueChange = {
                viewModel.onUIEvent(IndProfessionalViewModel.UIEvent.OnJobAddressValueChange(it))
            },
            labelText = stringResource(R.string.smart_ind_professional_job_address),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            }),
            isTextArea = true,
            isError = viewModel.uiState.addressError.first,
            errorMessage = stringResource(viewModel.uiState.addressError.second),
            isRequiredMessage = stringResource(R.string.smart_ind_professional_address_required)
        )
    }
}
