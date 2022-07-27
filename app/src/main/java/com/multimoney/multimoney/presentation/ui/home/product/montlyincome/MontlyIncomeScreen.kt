package com.multimoney.multimoney.presentation.ui.home.product.montlyincome

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
import com.multimoney.multimoney.R
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.home.product.montlyincome.MonthlyIncomeViewModel.BaseEvent.OnFormCompleted
import com.multimoney.multimoney.presentation.ui.home.product.montlyincome.MonthlyIncomeViewModel.UIEvent.OnProfessionValueChange
import com.multimoney.multimoney.presentation.uielement.CustomDropdown
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField

@Composable
fun MonthlyIncome(viewModel: MonthlyIncomeViewModel = hiltViewModel()) {

    // Properties
    val focusManager = LocalFocusManager.current

    LaunchedEffect(true) {
        viewModel.baseEvent.collect { event ->
            when (event) {
                is OnFormCompleted -> {
                    // enable or disable continue button
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.background)
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.home_credit_origination_monthly_income_title),
            modifier = Modifier.padding(top = 32.dp),
            style = Typography.h5.copy(fontWeight = FontWeight.SemiBold, fontSize = 22.sp),
            color = MultimoneyTheme.colors.labelText
        )

        val placeHolder = when (viewModel.country) {
            1 -> stringResource(id = R.string.home_credit_origination_monthly_income_income_el_salvador_hint)
            2 -> stringResource(id = R.string.home_credit_origination_monthly_income_income_guatemala_hint)
            3 -> stringResource(id = R.string.home_credit_origination_monthly_income_income_costa_rica_hint)
            else -> {
                stringResource(id = R.string.home_credit_origination_monthly_income_income_el_salvador_hint)
            }
        }

        CustomOutlinedTextField(
            modifier = Modifier.padding(top = 32.dp),
            labelText = stringResource(id = R.string.home_credit_origination_monthly_income_income_label),
            value = viewModel.uiState.income,
            leadingIcon = R.drawable.ic_money,
            placeHolder = placeHolder,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
            ), keyboardActions = KeyboardActions(onNext = {
                focusManager.clearFocus()
            }),
            isRequiredMessage = stringResource(id = R.string.home_credit_origination_monthly_income_required_income),
            onValueChange = { viewModel.onUIEvent(MonthlyIncomeViewModel.UIEvent.OnIncomeValueChange(it)) },
            onDebounceValidation = { viewModel.onUIEvent(MonthlyIncomeViewModel.UIEvent.OnValidForm) },
            isError = viewModel.uiState.incomeError.first,
            errorMessage = stringResource(id = viewModel.uiState.incomeError.second)
        )
        CustomDropdown(
            modifier = Modifier
                .wrapContentSize(Alignment.TopStart)
                .focusable(false),
            items = stringArrayResource(id = R.array.home_credit_origination_monthly_income_professions).toList(),
            value = viewModel.uiState.profession,
            onValueChange = { viewModel.onUIEvent(OnProfessionValueChange(it)) },
            labelText = stringResource(id = string.home_credit_origination_monthly_income_profession_label),
            placeHolder = stringResource(id = string.select)
        )
    }
}