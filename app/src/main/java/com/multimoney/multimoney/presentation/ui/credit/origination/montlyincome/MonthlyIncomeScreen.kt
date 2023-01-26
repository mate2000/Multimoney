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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.CreditStep
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnCallMutationSaveCreditFlowStep
import com.multimoney.multimoney.presentation.ui.credit.origination.montlyincome.MonthlyIncomeViewModel.BaseEvent.OnFormCompleted
import com.multimoney.multimoney.presentation.ui.credit.origination.montlyincome.MonthlyIncomeViewModel.Companion.DATE_FORMAT
import com.multimoney.multimoney.presentation.ui.credit.origination.montlyincome.MonthlyIncomeViewModel.Companion.DATE_MIN_DAY
import com.multimoney.multimoney.presentation.ui.credit.origination.montlyincome.MonthlyIncomeViewModel.Companion.DATE_MIN_MONTH
import com.multimoney.multimoney.presentation.ui.credit.origination.montlyincome.MonthlyIncomeViewModel.Companion.DATE_MIN_YEAR
import com.multimoney.multimoney.presentation.ui.credit.origination.montlyincome.MonthlyIncomeViewModel.UIEvent.OnCallCatalogs
import com.multimoney.multimoney.presentation.ui.credit.origination.montlyincome.MonthlyIncomeViewModel.UIEvent.OnDivisionOccupationValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.montlyincome.MonthlyIncomeViewModel.UIEvent.OnDivisionProfessionValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.montlyincome.MonthlyIncomeViewModel.UIEvent.OnDivisionDuiEmissionPlaceValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.montlyincome.MonthlyIncomeViewModel.UIEvent.OnDuiEmissionDateValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.montlyincome.MonthlyIncomeViewModel.UIEvent.OnDuiExpirationDateValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.montlyincome.MonthlyIncomeViewModel.UIEvent.OnIncomeValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.montlyincome.MonthlyIncomeViewModel.UIEvent.OnLoadCreditSteps
import com.multimoney.multimoney.presentation.ui.credit.origination.montlyincome.MonthlyIncomeViewModel.UIEvent.OnNextActionClick
import com.multimoney.multimoney.presentation.ui.credit.origination.montlyincome.MonthlyIncomeViewModel.UIEvent.OnValidForm
import com.multimoney.multimoney.presentation.uielement.CustomDatePicker
import com.multimoney.multimoney.presentation.uielement.CustomDropdown
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.util.getCurrencySymbol
import com.multimoney.multimoney.presentation.util.getPickedDateAsString
import com.multimoney.multimoney.presentation.util.transformation.formatMoney

@Composable
fun MonthlyIncomeScreen(
    sharedViewModel: CreditViewModel,
    viewModel: MonthlyIncomeViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

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
            text = stringResource(id = viewModel.uiState.titleResource),
            modifier = Modifier.padding(top = 8.dp),
            style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.titleText
        )

        if (viewModel.idBrand == Brand.ElSalvador.id) {
            CustomDatePicker(
                context = context,
                modifier = Modifier.padding(top = 24.dp),
                labelText = stringResource(id = R.string.credit_monthly_income_dui_emission_date_label),
                placeHolder = stringResource(id = R.string.credit_monthly_income_dui_date_placeholder),
                value = viewModel.uiState.duiEmissionDate,
                minYear = DATE_MIN_YEAR,
                minMonth = DATE_MIN_MONTH,
                minDay = DATE_MIN_DAY,
                trailingIcon = R.drawable.ic_calendar_voucher,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.clearFocus()
                }),
                onValueChange = { _, year, month, dayOfMonth ->
                    viewModel.onUIEvent(
                        OnDuiEmissionDateValueChange(
                            getPickedDateAsString(
                                year,
                                month,
                                dayOfMonth,
                                DATE_FORMAT
                            )
                        )
                    )
                }
            )

            CustomDropdown(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .wrapContentSize(Alignment.TopStart)
                    .focusable(false),
                items = viewModel.uiState.divisionDuiEmissionPlaceList,
                value = viewModel.uiState.divisionDuiEmissionPlaceSelected,
                onValueChange = { viewModel.onUIEvent(OnDivisionDuiEmissionPlaceValueChange(it)) },
                labelText = stringResource(id = R.string.credit_monthly_income_dui_emission_place_label),
                placeHolder = stringResource(id = R.string.credit_monthly_income_profession_hint)
            )

            CustomDatePicker(
                context = context,
                modifier = Modifier.padding(top = 16.dp),
                labelText = stringResource(id = R.string.credit_monthly_income_dui_expiration_date_label),
                placeHolder = stringResource(id = R.string.credit_monthly_income_dui_date_placeholder),
                value = viewModel.uiState.duiExpirationDate,
                minYear = viewModel.duiExpirationMinDate?.year ?: DATE_MIN_YEAR,
                minMonth = viewModel.duiExpirationMinDate?.monthValue ?: DATE_MIN_MONTH,
                minDay = viewModel.duiExpirationMinDate?.dayOfMonth ?: DATE_MIN_DAY,
                maxDateToday = false,
                trailingIcon = R.drawable.ic_calendar_voucher,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.clearFocus()
                }),
                onValueChange = { _, year, month, dayOfMonth ->
                    viewModel.onUIEvent(
                        OnDuiExpirationDateValueChange(
                            getPickedDateAsString(
                                year,
                                month,
                                dayOfMonth,
                                DATE_FORMAT
                            )
                        )
                    )
                }
            )
        }

        CustomOutlinedTextField(
            modifier = Modifier.padding(
                top = if (viewModel.idBrand == Brand.ElSalvador.id) {
                    16.dp
                } else {
                    24.dp
                }
            ),
            labelText = if (viewModel.idBrand == Brand.ElSalvador.id) {
                stringResource(id = R.string.credit_monthly_income_income_label)
            } else {
                null
            },
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
