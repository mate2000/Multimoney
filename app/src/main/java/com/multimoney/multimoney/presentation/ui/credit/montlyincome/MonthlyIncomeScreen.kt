package com.multimoney.multimoney.presentation.ui.credit.montlyincome

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.CreditStep
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.credit.CreditViewModel
import com.multimoney.multimoney.presentation.ui.credit.montlyincome.MonthlyIncomeViewModel.UIEvent.OnProfessionValueChange
import com.multimoney.multimoney.presentation.uielement.CustomDropdown
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
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
                is MonthlyIncomeViewModel.BaseEvent.OnFormCompleted -> {
                    sharedViewModel.onUIEvent(CreditViewModel.UIEvent.OnContinueEnable(event.isFormCompleted))
                }
            }
        }
    }

    LaunchedEffect(true) {
        viewModel.onUIEvent(MonthlyIncomeViewModel.UIEvent.OnValidForm)
        sharedViewModel.onUIEvent(CreditViewModel.UIEvent.OnSetNavigation(nextAction = {
            viewModel.onUIEvent(
                MonthlyIncomeViewModel.UIEvent.OnNextActionClick(
                    user = sharedViewModel.email,
                    nextStepAction = {
                        sharedViewModel.onUIEvent(
                            CreditViewModel.UIEvent.OnCallMutationSaveCreditFlowStep
                        )
                    },
                    saveCreditStepsHelper = sharedViewModel.saveCreditStepsHelper
                )
            )
        }, nextStep = CreditStep.Three.id, previousStep = CreditStep.One.id))
        viewModel.onUIEvent(MonthlyIncomeViewModel.UIEvent.OnLoadCreditSteps(sharedViewModel.screenConfig))
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
                    stringResource(id = viewModel.getCurrencySymbol(sharedViewModel.idBrand.toInt()))
                }
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
            ), keyboardActions = KeyboardActions(onNext = {
                focusManager.clearFocus()
            }),
            isRequiredMessage = stringResource(id = R.string.credit_monthly_income_required_income),
            onValueChange = {
                viewModel.onUIEvent(
                    MonthlyIncomeViewModel.UIEvent.OnIncomeValueChange(
                        it
                    )
                )
            },
            isError = viewModel.uiState.incomeError.first,
            errorMessage = stringResource(id = viewModel.uiState.incomeError.second),
            customTransformation = formatMoney(sharedViewModel.currencySymbol.ifEmpty {
                stringResource(id = viewModel.getCurrencySymbol(sharedViewModel.idBrand.toInt()))
            })
        )
        CustomDropdown(
            modifier = Modifier
                .padding(top = 16.dp)
                .wrapContentSize(Alignment.TopStart)
                .focusable(false),
            items = stringArrayResource(id = R.array.credit_monthly_income_professions).toList(),
            value = viewModel.uiState.profession,
            onValueChange = { viewModel.onUIEvent(OnProfessionValueChange(it)) },
            labelText = stringResource(id = R.string.credit_monthly_income_profession_label),
            placeHolder = stringResource(id = R.string.select)
        )
    }
}