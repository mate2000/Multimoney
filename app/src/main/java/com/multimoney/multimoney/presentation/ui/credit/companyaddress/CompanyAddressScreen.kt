package com.multimoney.multimoney.presentation.ui.credit.companyaddress

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.SmartStep
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.credit.CreditViewModel
import com.multimoney.multimoney.presentation.uielement.CustomDropdown
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField

@Composable
fun CompanyAddressScreen(
    sharedViewModel: CreditViewModel,
    viewModel: CompanyAddressViewModel = hiltViewModel()
) {

    val focusManager = LocalFocusManager.current

    var divisionOneText = ""
    var divisionTwoText = ""
    var divisionThreeText = ""

    when (sharedViewModel.idBrand.toInt()) {
        Brand.CostaRica.id -> {
            divisionOneText = stringResource(id = R.string.credit_address_province)
            divisionTwoText = stringResource(id = R.string.credit_address_canton)
            divisionThreeText = stringResource(id = R.string.credit_address_district)
        }
        Brand.Guatemala.id -> {
            divisionOneText = stringResource(id = R.string.credit_address_state)
            divisionTwoText = stringResource(id = R.string.credit_address_municipality)
            divisionThreeText = stringResource(id = R.string.credit_address_zone)
        }
        Brand.ElSalvador.id -> {
            divisionOneText = stringResource(id = R.string.credit_address_state)
            divisionTwoText = stringResource(id = R.string.credit_address_municipality)
        }
    }

    LaunchedEffect(true) {
        viewModel.baseEvent.collect { event ->
            when (event) {
                is CompanyAddressViewModel.BaseEvent.IsFormCompleted -> sharedViewModel.onUIEvent(
                    CreditViewModel.UIEvent.OnContinueEnable(
                        event.isCompleted
                    )
                )
            }
        }
    }

    LaunchedEffect(true) {
        sharedViewModel.onUIEvent(
            CreditViewModel.UIEvent.OnSetNavigation(
                nextAction = {
                    viewModel.onUIEvent(
                        CompanyAddressViewModel.UIEvent.OnNextActionClick(
                            user = sharedViewModel.email,
                            nextStepAction = {
                                sharedViewModel.onUIEvent(CreditViewModel.UIEvent.OnCallMutationSaveCreditFlowStep)
                            }, saveCreditStepsHelper = sharedViewModel.saveCreditStepsHelper
                        )
                    )
                },
                nextStep = SmartStep.Six.id, previousStep = SmartStep.Four.id
            )
        )
        viewModel.onUIEvent(CompanyAddressViewModel.UIEvent.OnFormValid)
        viewModel.onUIEvent(
            CompanyAddressViewModel.UIEvent.OnCallCatalogs(
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
        viewModel.onUIEvent(
            CompanyAddressViewModel.UIEvent.OnLoadCreditSteps(sharedViewModel.saveCreditStepsHelper.inputTextInfoList)
        )
    }

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = stringResource(id = R.string.credit_company_address_title),
            modifier = Modifier.padding(top = 16.dp),
            style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.labelText
        )
        CustomDropdown(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp),
            items = viewModel.uiState.divisionOneList,
            value = viewModel.uiState.divisionOneSelected,
            onValueChange = {
                viewModel.onUIEvent(
                    CompanyAddressViewModel.UIEvent.OnDivisionOneValueChange(
                        divisionOne = it,
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
            },
            labelText = divisionOneText,
            placeHolder = stringResource(id = R.string.select)
        )
        CustomDropdown(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            items = viewModel.uiState.divisionTwoList,
            value = viewModel.uiState.divisionTwoSelected,
            onValueChange = {
                viewModel.onUIEvent(
                    CompanyAddressViewModel.UIEvent.OnDivisionTwoValueChange(
                        divisionTwo = it,
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
            },
            labelText = divisionTwoText,
            placeHolder = stringResource(id = R.string.select)
        )
        if (sharedViewModel.idBrand.toInt() != Brand.ElSalvador.id) {
            CustomDropdown(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                items = viewModel.uiState.divisionThreeList,
                value = viewModel.uiState.divisionThreeSelected,
                onValueChange = {
                    viewModel.onUIEvent(
                        CompanyAddressViewModel.UIEvent.OnDivisionThreeValueChange(
                            it
                        )
                    )
                },
                labelText = divisionThreeText,
                placeHolder = stringResource(id = R.string.select)
            )
        }
        CustomOutlinedTextField(
            modifier = Modifier.padding(top = 16.dp),
            labelText = stringResource(id = R.string.credit_address_accurate_address),
            value = viewModel.uiState.address,
            onValueChange = {
                viewModel.onUIEvent(
                    CompanyAddressViewModel.UIEvent.OnAddressValueChange(
                        it
                    )
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            }),
            isRequired = true,
            isError = viewModel.uiState.addressError.first,
            isRequiredMessage = stringResource(id = viewModel.uiState.addressError.second),
            isTextArea = true
        )
    }
}