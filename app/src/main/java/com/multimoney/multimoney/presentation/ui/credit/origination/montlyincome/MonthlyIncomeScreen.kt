package com.multimoney.multimoney.presentation.ui.credit.origination.montlyincome

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.CreditStep
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnCallMutationSaveCreditFlowStep
import com.multimoney.multimoney.presentation.ui.credit.origination.montlyincome.MonthlyIncomeViewModel.BaseEvent.OnFormCompleted
import com.multimoney.multimoney.presentation.ui.credit.origination.montlyincome.MonthlyIncomeViewModel.UIEvent.OnCallCatalogs
import com.multimoney.multimoney.presentation.ui.credit.origination.montlyincome.MonthlyIncomeViewModel.UIEvent.OnDivisionOccupationValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.montlyincome.MonthlyIncomeViewModel.UIEvent.OnDivisionProfessionValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.montlyincome.MonthlyIncomeViewModel.UIEvent.OnIncomeValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.montlyincome.MonthlyIncomeViewModel.UIEvent.OnLoadCreditSteps
import com.multimoney.multimoney.presentation.ui.credit.origination.montlyincome.MonthlyIncomeViewModel.UIEvent.OnNextActionClick
import com.multimoney.multimoney.presentation.ui.credit.origination.montlyincome.MonthlyIncomeViewModel.UIEvent.OnValidForm
import com.multimoney.multimoney.presentation.uielement.CustomDropdown
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.util.getCurrencySymbol
import com.multimoney.multimoney.presentation.util.transformation.formatMoney

@Composable
fun MonthlyIncomeScreen(
    sharedViewModel: CreditViewModel,
    viewModel: MonthlyIncomeViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current

    LaunchedEffect(true) {
        viewModel.baseEvent.collect { event ->
            when (event) {
                is OnFormCompleted -> {
                    sharedViewModel.onUIEvent(CreditViewModel.UIEvent.OnContinueEnable(event.isFormCompleted))
                }
            }
        }
    }

    LaunchedEffect(true) {
        viewModel.onUIEvent(OnValidForm)
        sharedViewModel.onUIEvent(
            CreditViewModel.UIEvent.OnSetNavigation(nextAction = {
                viewModel.onUIEvent(
                    OnNextActionClick(
                        user = sharedViewModel.email,
                        nextStepAction = {
                            sharedViewModel.onUIEvent(OnCallMutationSaveCreditFlowStep)
                        },
                        saveCreditStepsHelper = sharedViewModel.saveCreditStepsHelper
                    )
                )
            }, nextStep = CreditStep.Four.id, previousStep = CreditStep.Two.id)
        )
        viewModel.onUIEvent(OnLoadCreditSteps(sharedViewModel.saveCreditStepsHelper.inputTextInfoList))
    }

    LaunchedEffect(true) {
        viewModel.onUIEvent(
            OnCallCatalogs(
                sharedViewModel.pkUser,
                sharedViewModel.email,
                sharedViewModel.idBrand.toInt(),
                idUserRequest = sharedViewModel.idUserRequest,
                onLoadingValueChange = { isLoading ->
                    sharedViewModel.onUIEvent(CreditViewModel.UIEvent.OnLoadingValueChange(isLoading))
                },
                onFailureWithDialog = { isLoading, dialogParameters ->
                    sharedViewModel.onUIEvent(
                        CreditViewModel.UIEvent.OnFailureWithDialog(
                            isLoading,
                            dialogParameters
                        )
                    )
                }
            )
        )
        viewModel.onUIEvent(OnLoadCreditSteps(sharedViewModel.saveCreditStepsHelper.inputTextInfoList))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.background)
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.credit_monthly_income_title),
            modifier = Modifier.padding(top = 32.dp),
            style = Typography.h5.copy(fontWeight = FontWeight.SemiBold, fontSize = 22.sp),
            color = MultimoneyTheme.colors.labelText
        )

        CustomOutlinedTextField(
            modifier = Modifier.padding(top = 32.dp),
            value = viewModel.uiState.income,
            leadingIcon = R.drawable.ic_money,
            placeHolder = stringResource(
                id = R.string.credit_monthly_income_income_hint,
                sharedViewModel.currencySymbol.ifEmpty {
                    stringResource(id = sharedViewModel.idBrand.toInt().getCurrencySymbol())
                }
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.clearFocus()
            }),
            isRequiredMessage = stringResource(id = R.string.credit_monthly_income_required_income),
            onValueChange = {
                viewModel.onUIEvent(
                    OnIncomeValueChange(
                        it
                    )
                )
            },
            isError = viewModel.uiState.incomeError.first,
            errorMessage = stringResource(id = viewModel.uiState.incomeError.second),
            customTransformation = formatMoney(
                sharedViewModel.currencySymbol.ifEmpty {
                    stringResource(id = sharedViewModel.idBrand.toInt().getCurrencySymbol())
                }
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        CustomDropdown(
            modifier = Modifier
                .wrapContentSize(Alignment.TopStart)
                .focusable(false),
            items = viewModel.uiState.divisionProfessionList,
            value = viewModel.uiState.divisionProfessionSelected,
            onValueChange = { viewModel.onUIEvent(OnDivisionProfessionValueChange(it)) },
            labelText = stringResource(id = R.string.credit_monthly_income_profession_label),
            placeHolder = stringResource(id = R.string.credit_monthly_income_profession_hint)
        )
        if (viewModel.idBrand == Brand.CostaRica.id) {
            Spacer(modifier = Modifier.height(16.dp))
            CustomDropdown(
                modifier = Modifier
                    .wrapContentSize(Alignment.TopStart)
                    .focusable(false),
                items = viewModel.uiState.divisionOccupationList,
                value = viewModel.uiState.divisionOccupationSelected,
                onValueChange = { viewModel.onUIEvent(OnDivisionOccupationValueChange(it)) },
                labelText = stringResource(id = R.string.credit_monthly_income_occupation_label),
                placeHolder = stringResource(id = R.string.credit_monthly_income_occupation_hint)
            )
        }
    }
}
