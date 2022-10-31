package com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.ownbusiness

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.SourceIncomeViewModel.UIEvent.OnNavigateToSelectedSourceOfIncomeOption
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.ownbusiness.OwnBusinessViewModel.BaseEvent.OnFormValidateCompleted
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.ownbusiness.OwnBusinessViewModel.UIEvent.OnCompanyDescriptionChange
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.ownbusiness.OwnBusinessViewModel.UIEvent.OnCompanyNameChange
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.ownbusiness.OwnBusinessViewModel.UIEvent.OnMonthlyIncomeChange
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.catalog.SourceIncomeOptionType
import com.multimoney.multimoney.presentation.util.getCurrencySymbol
import com.multimoney.multimoney.presentation.util.transformation.formatMoney

@Composable
@Preview
fun SmartOwnBusinessSvScreen(
    viewModel: OwnBusinessViewModel = hiltViewModel(),
    sharedViewModel: SmartViewModel = hiltViewModel(),
    sourceIncomeSharedViewModel: SourceIncomeViewModel = hiltViewModel(),
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
) {
    LaunchedEffect(true) {

        sharedViewModel.onUIEvent(OnContinueEnable(viewModel.isFormValid()))
        sharedViewModel.onUIEvent(OnContinueVisible(true))

        sharedViewModel.onUIEvent(
            OnSetNavigation(
                nextAction = {
                    sharedViewModel.onUIEvent(
                        OnCallMutationUpdateGlobalRequestUseCase(
                            accountSmartData = sharedViewModel.accountSmartData?.copy(
                                companyName = viewModel.uiState.companyNameValue,
                                aboutCompany = viewModel.uiState.companyDescriptionValue,
                                income = viewModel.uiState.monthlyIncomeValue.toFloat(),
                                currentStep = SmartSteps.Search.getNameById(sharedViewModel.uiState.currentStep)
                            )
                        )
                    )
                },
                nextStep = SmartSteps.Four.id,
                previousStep = SmartSteps.Three.id
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
    SmartOwnBusinessSvContent(viewModel, sharedViewModel)

    // return to the main options screen whenever tapping on native back button from the device
    BackHandler {
        sourceIncomeSharedViewModel.onUIEvent(
            (OnNavigateToSelectedSourceOfIncomeOption(
                SourceIncomeOptionType.MainSourceIncomeScreenType.id
            ))
        )
    }
}

@Composable
fun SmartOwnBusinessSvContent(
    viewModel: OwnBusinessViewModel,
    sharedViewModel: SmartViewModel
) {
    val focusManager = LocalFocusManager.current
    Column(
        modifier = Modifier
            .padding(vertical = 16.dp, horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(R.string.smart_own_business_title),
            style = Typography.h6.copy(
                fontWeight = FontWeight.SemiBold,
                color = MultimoneyTheme.colors.text
            )
        )
        CustomOutlinedTextField(
            value = viewModel.uiState.companyNameValue,
            onValueChange = { companyName ->
                viewModel.onUIEvent(OnCompanyNameChange(companyName = companyName))
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            }),
            labelText = stringResource(id = R.string.smart_own_business_company_name_label),
            isRequired = true,
            isRequiredMessage = stringResource(R.string.smart_own_business_company_name_required),
            modifier = Modifier.padding(top = 24.dp)
        )
        CustomOutlinedTextField(
            value = viewModel.uiState.companyDescriptionValue,
            onValueChange = { description ->
                viewModel.onUIEvent(OnCompanyDescriptionChange(description = description))
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            }),
            labelText = stringResource(id = R.string.smart_own_business_description_label),
            isRequired = true,
            isRequiredMessage = stringResource(R.string.smart_own_business_description_required),
            isTextArea = true,
            isError = viewModel.uiState.companyDescriptionError.first,
            errorMessage = stringResource(viewModel.uiState.companyDescriptionError.second),
            modifier = Modifier.padding(top = 16.dp)
        )
        CustomOutlinedTextField(
            value = viewModel.uiState.monthlyIncomeValue,
            onValueChange = { monthlyIncome ->
                viewModel.onUIEvent(OnMonthlyIncomeChange(monthlyIncome = monthlyIncome))
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            }),
            labelText = stringResource(id = R.string.smart_own_business_monthly_income_label),
            isRequired = true,
            isRequiredMessage = stringResource(R.string.smart_own_business_monthly_income_required),
            leadingIcon = R.drawable.ic_money_gray,
            placeHolder = stringResource(
                id = R.string.smart_own_business_monthly_income_placeholder,
                stringResource(sharedViewModel.idBrand.toInt().getCurrencySymbol())
            ),
            customTransformation = formatMoney(
                stringResource(
                    sharedViewModel.idBrand.toInt().getCurrencySymbol()
                )
            ),
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}
