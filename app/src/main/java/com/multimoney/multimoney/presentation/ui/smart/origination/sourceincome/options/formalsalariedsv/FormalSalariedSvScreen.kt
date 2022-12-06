package com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.formalsalariedsv

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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.SmartSteps.Search
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnCallMutationUpdateGlobalRequestUseCase
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnContinueEnable
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnContinueVisible
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnFailureWithDialog
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnSetNavigation
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.SourceIncomeViewModel
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.SourceIncomeViewModel.UIEvent.OnNavigateToSelectedSourceOfIncomeOption
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.formalsalariedsv.FormalSalariedSvViewModel.BaseEvent.OnFormValidateCompleted
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.formalsalariedsv.FormalSalariedSvViewModel.BaseEvent.OnRequestError
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.formalsalariedsv.FormalSalariedSvViewModel.UIEvent.OnCompanyNameChange
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.formalsalariedsv.FormalSalariedSvViewModel.UIEvent.OnDivisionThreeValueChange
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.formalsalariedsv.FormalSalariedSvViewModel.UIEvent.OnDivisionTwoValueChange
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.formalsalariedsv.FormalSalariedSvViewModel.UIEvent.OnGetUserData
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.formalsalariedsv.FormalSalariedSvViewModel.UIEvent.OnProfessionChange
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.formalsalariedsv.FormalSalariedSvViewModel.UIEvent.OnSalaryChange
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.formalsalariedsv.FormalSalariedSvViewModel.UIEvent.OnWorkingAddressChange
import com.multimoney.multimoney.presentation.uielement.CustomDropdown
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.util.catalog.SourceIncomeOptionType
import com.multimoney.multimoney.presentation.util.catalog.SourceIncomeOptionType.MainSourceIncomeScreenType
import com.multimoney.multimoney.presentation.util.getCurrencySymbol
import com.multimoney.multimoney.presentation.util.transformation.formatDecimalMoney

@Composable
fun FormalSalariedSvScreen(
    sharedViewModel: SmartViewModel = hiltViewModel(),
    sourceIncomeSharedViewModel: SourceIncomeViewModel = hiltViewModel(),
    viewModel: FormalSalariedSvViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = true) {
        sharedViewModel.onUIEvent(OnContinueVisible(true))
        sharedViewModel.onUIEvent(OnContinueEnable(viewModel.isFormValid()))
        viewModel.onUiEvent(OnGetUserData(sharedViewModel.user, sharedViewModel.idBrandAsInt))

        sharedViewModel.onUIEvent(
            OnSetNavigation(
                nextAction = {
                    sharedViewModel.onUIEvent(
                        OnCallMutationUpdateGlobalRequestUseCase(
                            accountSmartData = sharedViewModel.accountSmartData?.copy(
                                idEconomicActivity = SourceIncomeOptionType.FormalSalariedSv.id.toLong(),
                                income = viewModel.uiState.salary.toFloat(),
                                companyName = viewModel.uiState.companyName,
                                positionJob = viewModel.uiState.profession,
                                idJobLevel2 = viewModel.uiState.divisionTwoSelected?.id?.toLong() ?: 0,
                                idJobLevel3 = viewModel.uiState.divisionThreeSelected?.id?.toLong() ?: 0,
                                currentStep = Search.getNameById(sharedViewModel.uiState.currentStep)
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
                is OnFormValidateCompleted -> sharedViewModel.onUIEvent(
                    OnContinueEnable(event.isFormValid)
                )

                is OnRequestError -> sharedViewModel.onUIEvent(
                    OnFailureWithDialog(
                        isLoading = false,
                        openDialog = event.dialogParameters
                    )
                )
            }
        }
    }

    FormalSalariedSvContent(viewModel, sharedViewModel.idBrandAsInt)
    BackHandler {
        sourceIncomeSharedViewModel.onUIEvent(
            OnNavigateToSelectedSourceOfIncomeOption(
                MainSourceIncomeScreenType.id
            )
        )
    }
}

@Composable
fun FormalSalariedSvContent(
    viewModel: FormalSalariedSvViewModel,
    idBrand: Int
) {
    val focusManager = LocalFocusManager.current
    val currencySymbol = stringResource(idBrand.getCurrencySymbol())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(R.string.smart_salaried_title),
            style = Typography.h6.copy(
                fontWeight = FontWeight.SemiBold,
                color = MultimoneyTheme.colors.text
            )
        )

        CustomOutlinedTextField(
            value = viewModel.uiState.companyName,
            onValueChange = { viewModel.onUiEvent(OnCompanyNameChange(it)) },
            labelText = stringResource(R.string.smart_salaried_company_name),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            }),
            isRequiredMessage = stringResource(R.string.smart_salaried_company_name_required),
            modifier = Modifier.padding(top = 24.dp)
        )

        CustomOutlinedTextField(
            value = viewModel.uiState.profession,
            onValueChange = { viewModel.onUiEvent(OnProfessionChange(it)) },
            labelText = stringResource(R.string.smart_salaried_profession),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            }),
            isRequiredMessage = stringResource(R.string.smart_salaried_profession_required),
            modifier = Modifier.padding(top = 16.dp)
        )

        CustomOutlinedTextField(
            value = viewModel.uiState.salary,
            onValueChange = { viewModel.onUiEvent(OnSalaryChange(it)) },
            labelText = stringResource(R.string.smart_salaried_average_salary),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            }),
            leadingIcon = R.drawable.ic_money_gray,
            placeHolder = stringResource(R.string.smart_salaried_salary_placeholder, currencySymbol),
            customTransformation = formatDecimalMoney(currencySymbol),
            isRequiredMessage = stringResource(R.string.smart_salaried_average_salary_required),
            modifier = Modifier.padding(top = 16.dp)
        )

        CustomDropdown(
            items = viewModel.uiState.divisionTwoList?.map { it?.name.orEmpty() } ?: listOf(),
            value = viewModel.uiState.divisionTwoSelected?.name ?: stringResource(R.string.empty),
            onValueChange = {
                viewModel.onUiEvent(
                    OnDivisionTwoValueChange(divisionTwo = it)
                )
            },
            labelText = stringResource(R.string.credit_address_state),
            placeHolder = stringResource(id = R.string.select),
            modifier = Modifier.padding(top = 16.dp)
        )

        CustomDropdown(
            items = viewModel.uiState.divisionThreeList?.map { it?.name.orEmpty() } ?: listOf(),
            value = viewModel.uiState.divisionThreeSelected?.name ?: stringResource(R.string.empty),
            onValueChange = {
                viewModel.onUiEvent(
                    OnDivisionThreeValueChange(divisionThree = it)
                )
            },
            labelText = stringResource(R.string.credit_address_municipality),
            placeHolder = stringResource(id = R.string.select),
            modifier = Modifier.padding(top = 16.dp)
        )

        CustomOutlinedTextField(
            value = viewModel.uiState.workingAddress,
            onValueChange = { viewModel.onUiEvent(OnWorkingAddressChange(it)) },
            labelText = stringResource(R.string.smart_salaried_working_address),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            }),
            isTextArea = true,
            isError = viewModel.uiState.workingAddressError.first,
            errorMessage = stringResource(viewModel.uiState.workingAddressError.second),
            isRequiredMessage = stringResource(R.string.smart_salaried_working_address_required),
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}
