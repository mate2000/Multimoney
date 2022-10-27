package com.multimoney.multimoney.presentation.ui.credit.origination.homeaddress

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
import com.multimoney.data.util.catalog.SmartStep.Five
import com.multimoney.data.util.catalog.SmartStep.Seven
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnCallMutationSaveCreditFlowStep
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnContinueEnable
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnFailureWithDialog
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnLoadingValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.homeaddress.HomeAddressViewModel.BaseEvent.IsFormCompleted
import com.multimoney.multimoney.presentation.ui.credit.origination.homeaddress.HomeAddressViewModel.UIEvent.OnAddressValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.homeaddress.HomeAddressViewModel.UIEvent.OnCallCatalogs
import com.multimoney.multimoney.presentation.ui.credit.origination.homeaddress.HomeAddressViewModel.UIEvent.OnDivisionOneValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.homeaddress.HomeAddressViewModel.UIEvent.OnDivisionThreeValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.homeaddress.HomeAddressViewModel.UIEvent.OnDivisionTwoValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.homeaddress.HomeAddressViewModel.UIEvent.OnFormValid
import com.multimoney.multimoney.presentation.ui.credit.origination.homeaddress.HomeAddressViewModel.UIEvent.OnLoadCreditSteps
import com.multimoney.multimoney.presentation.ui.credit.origination.homeaddress.HomeAddressViewModel.UIEvent.OnNextActionClick
import com.multimoney.multimoney.presentation.ui.credit.origination.homeaddress.HomeAddressViewModel.UIEvent.OnPhoneNumberValueChange
import com.multimoney.multimoney.presentation.uielement.CustomDropdown
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.util.VisualTransformationMasks.PHONE_TRANSFORMATION_MASK
import com.multimoney.multimoney.presentation.util.transformation.MaskVisualTransformation

@Composable
fun HomeAddressScreen(
    sharedViewModel: CreditViewModel,
    viewModel: HomeAddressViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current

    var divisionOneText = ""
    var divisionTwoText = ""
    var divisionThreeText = ""
    var title = R.string.credit_home_address_title

    when (sharedViewModel.idBrand.toInt()) {
        Brand.CostaRica.id -> {
            divisionOneText = stringResource(id = R.string.credit_address_province)
            divisionTwoText = stringResource(id = R.string.credit_address_canton)
            divisionThreeText = stringResource(id = R.string.credit_address_district)
            title = R.string.credit_home_address_title
        }
        Brand.Guatemala.id -> {
            divisionOneText = stringResource(id = R.string.credit_address_state)
            divisionTwoText = stringResource(id = R.string.credit_address_municipality)
            divisionThreeText = stringResource(id = R.string.credit_address_zone)
            title = R.string.credit_home_address_title_gt
        }
        Brand.ElSalvador.id -> {
            divisionOneText = stringResource(id = R.string.credit_address_state)
            divisionTwoText = stringResource(id = R.string.credit_address_municipality)
            title = R.string.credit_home_address_title
        }
    }

    LaunchedEffect(true) {
        viewModel.baseEvent.collect { event ->
            when (event) {
                is IsFormCompleted -> sharedViewModel.onUIEvent(OnContinueEnable(event.isCompleted))
            }
        }
    }

    LaunchedEffect(true) {
        sharedViewModel.onUIEvent(
            CreditViewModel.UIEvent.OnSetNavigation(
                nextAction = {
                    viewModel.onUIEvent(
                        OnNextActionClick(
                            user = sharedViewModel.email,
                            nextStepAction = {
                                sharedViewModel.onUIEvent(OnCallMutationSaveCreditFlowStep)
                            },
                            saveCreditStepsHelper = sharedViewModel.saveCreditStepsHelper
                        )
                    )
                },
                nextStep = Seven.id,
                previousStep = Five.id
            )
        )
        viewModel.onUIEvent(OnFormValid)
        viewModel.onUIEvent(
            OnCallCatalogs(
                sharedViewModel.pkUser,
                sharedViewModel.email,
                sharedViewModel.idBrand.toInt(),
                idUserRequest = sharedViewModel.idUserRequest,
                onLoadingValueChange = { isLoading ->
                    sharedViewModel.onUIEvent(OnLoadingValueChange(isLoading))
                },
                onFailureWithDialog = { isLoading, dialogParameters ->
                    sharedViewModel.onUIEvent(OnFailureWithDialog(isLoading, dialogParameters))
                }
            )
        )
        viewModel.onUIEvent(OnLoadCreditSteps(sharedViewModel.saveCreditStepsHelper.inputTextInfoList))
    }

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = stringResource(id = title),
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
                    OnDivisionOneValueChange(
                        divisionOne = it,
                        onLoadingValueChange = { isLoading ->
                            sharedViewModel.onUIEvent(OnLoadingValueChange(isLoading))
                        },
                        onFailureWithDialog = { isLoading, dialogParameters ->
                            sharedViewModel.onUIEvent(OnFailureWithDialog(isLoading, dialogParameters))
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
                    OnDivisionTwoValueChange(
                        divisionTwo = it,
                        onLoadingValueChange = { isLoading ->
                            sharedViewModel.onUIEvent(OnLoadingValueChange(isLoading))
                        },
                        onFailureWithDialog = { isLoading, dialogParameters ->
                            sharedViewModel.onUIEvent(OnFailureWithDialog(isLoading, dialogParameters))
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
                onValueChange = { viewModel.onUIEvent(OnDivisionThreeValueChange(it)) },
                labelText = divisionThreeText,
                placeHolder = stringResource(id = R.string.select)
            )
        }
        CustomOutlinedTextField(
            modifier = Modifier.padding(top = 16.dp),
            labelText = stringResource(id = R.string.credit_address_accurate_address),
            value = viewModel.uiState.address,
            onValueChange = { viewModel.onUIEvent(OnAddressValueChange(it)) },
            keyboardOptions = if (sharedViewModel.idBrand.toInt() != Brand.CostaRica.id) KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ) else KeyboardOptions(
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

        val phonePlaceHolder = when (sharedViewModel.idBrand.toInt()) {
            Brand.ElSalvador.id -> R.string.credit_job_phone_placeholder_sv
            Brand.CostaRica.id -> R.string.credit_job_phone_placeholder_cr
            Brand.Guatemala.id -> R.string.credit_job_phone_placeholder_gt
            else -> R.string.empty
        }

        if (sharedViewModel.idBrand.toInt() != Brand.CostaRica.id) {
            CustomOutlinedTextField(
                leadingIcon = R.drawable.ic_phone,
                value = viewModel.uiState.phone,
                placeHolder = stringResource(id = phonePlaceHolder),
                onValueChange = { phoneNumber ->
                    viewModel.onUIEvent(OnPhoneNumberValueChange(phoneNumber))
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                }),
                labelText = stringResource(id = R.string.credit_job_phone_number),
                modifier = Modifier.padding(top = 16.dp),
                isRequired = true,
                isRequiredMessage = stringResource(id = R.string.credit_job_phone_required),
                customTransformation = MaskVisualTransformation(
                    PHONE_TRANSFORMATION_MASK.mask,
                    PHONE_TRANSFORMATION_MASK.maskChar
                )
            )
        }
    }
}
