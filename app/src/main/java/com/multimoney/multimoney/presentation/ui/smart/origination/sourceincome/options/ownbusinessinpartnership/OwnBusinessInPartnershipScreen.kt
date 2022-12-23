package com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.ownbusinessinpartnership

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
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
import com.multimoney.data.util.catalog.SmartSteps
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnCallMutationUpdateGlobalRequestUseCase
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnContinueEnable
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnContinueVisible
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnLoadingValueChange
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnSetNavigation
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.SmartAddressFields
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.SourceIncomeViewModel
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.SourceIncomeViewModel.BaseEvent
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.SourceIncomeViewModel.UIEvent.OnNavigateToSelectedSourceOfIncomeOption
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.ownbusinessinpartnership.OwnBusinessInPartnershipViewModel.BaseEvent.OnFormValidateCompleted
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.ownbusinessinpartnership.OwnBusinessInPartnershipViewModel.UIEvent.OnBusinessActivityChange
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.ownbusinessinpartnership.OwnBusinessInPartnershipViewModel.UIEvent.OnIdentificationChange
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.ownbusinessinpartnership.OwnBusinessInPartnershipViewModel.UIEvent.OnIncomeAmountChange
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.ownbusinessinpartnership.OwnBusinessInPartnershipViewModel.UIEvent.OnLoadCurrentStepData
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.util.catalog.SourceIncomeOptionType
import com.multimoney.multimoney.presentation.util.catalog.SourceIncomeOptionType.MainSourceIncomeScreenType
import com.multimoney.multimoney.presentation.util.getCurrencySymbol
import com.multimoney.multimoney.presentation.util.transformation.formatBusinessIdentification
import com.multimoney.multimoney.presentation.util.transformation.formatDecimalMoney

@Composable
fun OwnBusinessInPartnershipScreen(
    viewModel: OwnBusinessInPartnershipViewModel = hiltViewModel(),
    sharedViewModel: SmartViewModel = hiltViewModel(),
    sourceIncomeSharedViewModel: SourceIncomeViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {
        sourceIncomeSharedViewModel.baseEvent.collect { event ->
            when (event) {
                is BaseEvent.OnFormValidateCompleted -> sharedViewModel.onUIEvent(
                    OnContinueEnable(event.isFormValid && viewModel.isFormValid())
                )
            }
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.onUIEvent(OnLoadCurrentStepData(sharedViewModel.accountSmartData))
        sharedViewModel.onUIEvent(OnContinueVisible(true))
        sharedViewModel.onUIEvent(
            OnContinueEnable(
                viewModel.isFormValid() && sourceIncomeSharedViewModel.isFormValid()
            )
        )

        sharedViewModel.onUIEvent(
            OnSetNavigation(
                nextAction = {
                    sharedViewModel.onUIEvent(
                        OnCallMutationUpdateGlobalRequestUseCase(
                            accountSmartData = sharedViewModel.accountSmartData?.copy(
                                idEconomicActivity = SourceIncomeOptionType.OwnBusinessOnPersonalBasis.id.toLong(),
                                income = viewModel.uiState.businessIncome.toFloat(),
                                legalID = viewModel.uiState.businessIdentification,
                                entrepreneurship = viewModel.uiState.businessActivity,
                                currentStep = SmartSteps.Search.getNameById(sharedViewModel.uiState.currentStep),
                                idJobLevel1 = sourceIncomeSharedViewModel.uiState.divisionOneSelected?.id?.toLongOrNull(),
                                idJobLevel2 = sourceIncomeSharedViewModel.uiState.divisionTwoSelected?.id?.toLongOrNull(),
                                idJobLevel3 = sourceIncomeSharedViewModel.uiState.divisionThreeSelected?.id?.toLongOrNull(),
                                fullJobAddress = sourceIncomeSharedViewModel.uiState.address
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
                    OnContinueEnable(event.isFormValid && sourceIncomeSharedViewModel.isFormValid())
                )
            }
        }
    }

    sharedViewModel.onUIEvent(OnLoadingValueChange(viewModel.uiState.isLoading))
    OwnBusinessOnPersonalBasisContent(
        viewModel,
        sourceIncomeSharedViewModel,
        sharedViewModel.idBrandAsInt,
        sharedViewModel.user
    )

    BackHandler {
        sourceIncomeSharedViewModel.onUIEvent(
            OnNavigateToSelectedSourceOfIncomeOption(
                MainSourceIncomeScreenType.id
            )
        )
    }
}

@Composable
fun OwnBusinessOnPersonalBasisContent(
    viewModel: OwnBusinessInPartnershipViewModel,
    sourceIncomeSharedViewModel: SourceIncomeViewModel,
    idBrand: Int,
    user: String
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
            text = stringResource(R.string.smart_business_personal_basis_title),
            style = Typography.h6.copy(
                fontWeight = FontWeight.SemiBold,
                color = MultimoneyTheme.colors.text
            )
        )
        CustomOutlinedTextField(
            modifier = Modifier.padding(vertical = 8.dp),
            value = viewModel.uiState.businessActivity,
            onValueChange = {
                viewModel.onUIEvent(
                    OnBusinessActivityChange(it)
                )
            },
            labelText = stringResource(R.string.smart_business_personal_basis_activity_label),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.moveFocus(FocusDirection.Down)
            }),
            isTextArea = true,
            isError = viewModel.uiState.businessActivityError.first,
            errorMessage = stringResource(viewModel.uiState.businessActivityError.second),
            isRequiredMessage = stringResource(R.string.smart_business_personal_basis_activity_required_message)
        )

        CustomOutlinedTextField(
            modifier = Modifier.padding(vertical = 8.dp),
            value = viewModel.uiState.businessIncome,
            onValueChange = {
                viewModel.onUIEvent(
                    OnIncomeAmountChange(it)
                )
            },
            labelText = stringResource(R.string.credit_monthly_income_income_label),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.moveFocus(FocusDirection.Down)
            }),
            placeHolder = stringResource(
                R.string.smart_decimal_income_placeholder,
                currencySymbol
            ),
            leadingIcon = R.drawable.ic_money_gray,
            customTransformation = formatDecimalMoney(currencySymbol),
            isRequired = true,
            isRequiredMessage = stringResource(R.string.smart_business_personal_basis_income_required_message)
        )

        CustomOutlinedTextField(
            modifier = Modifier.padding(vertical = 8.dp),
            value = viewModel.uiState.businessIdentification,
            onValueChange = {
                viewModel.onUIEvent(
                    OnIdentificationChange(
                        it,
                        idBrand,
                        user
                    )
                )
            },
            labelText = stringResource(R.string.smart_business_personal_basis_identification_label),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
            }),
            placeHolder = stringResource(R.string.smart_business_personal_basis_identification_placeholder),
            customTransformation = formatBusinessIdentification(),
            isError = viewModel.uiState.identificationError.first,
            errorMessage = viewModel.uiState.identificationValidationError ?: stringResource(
                viewModel.uiState.identificationError.second
            ),
            showInfo = viewModel.uiState.identificationLoading.first,
            infoMessage = stringResource(viewModel.uiState.identificationLoading.second),
            isSuccess = viewModel.uiState.identificationSuccess.first,
            successMessage = stringResource(viewModel.uiState.identificationSuccess.second),
            isRequiredMessage = stringResource(R.string.smart_business_personal_basis_identification_required_message),
            enabled = !viewModel.uiState.isLoading
        )
        if (viewModel.uiState.identificationSuccess.first) {
            Text(
                text = viewModel.uiState.companyName
                    ?: stringResource(R.string.smart_business_personal_basis_company_name_not_found),
                color = MultimoneyTheme.colors.text,
                modifier = Modifier
                    .padding(start = 5.dp)
                    .wrapContentSize(),
                style = Typography.caption
            )
        }
        SmartAddressFields(
            sourceIncomeSharedViewModel = sourceIncomeSharedViewModel,
            user = user,
            idBrand = idBrand,
            focusManager = focusManager
        )
    }
}
