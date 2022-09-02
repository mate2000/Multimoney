package com.multimoney.multimoney.presentation.ui.credit.homeaddress

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
import com.multimoney.data.util.catalog.CreditStep.Four
import com.multimoney.data.util.catalog.CreditStep.Six
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.credit.CreditViewModel
import com.multimoney.multimoney.presentation.ui.credit.CreditViewModel.UIEvent.OnContinueEnable
import com.multimoney.multimoney.presentation.ui.credit.CreditViewModel.UIEvent.OnFailureWithDialog
import com.multimoney.multimoney.presentation.ui.credit.CreditViewModel.UIEvent.OnLoadingValueChange
import com.multimoney.multimoney.presentation.ui.credit.companyaddress.CompanyAddressViewModel
import com.multimoney.multimoney.presentation.ui.credit.companyaddress.PK_USER
import com.multimoney.multimoney.presentation.ui.credit.companyaddress.USER
import com.multimoney.multimoney.presentation.uielement.CustomDropdown
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HomeAddressScreen(
    sharedViewModel: CreditViewModel,
    viewModel: HomeAddressViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current

    var divisionOneText = ""
    var divisionTwoText = ""
    var divisionThreeText = ""

    when (viewModel.country) {
        CompanyAddressViewModel.ZERO -> {
            divisionOneText = stringResource(id = string.credit_address_province)
            divisionTwoText = stringResource(id = string.credit_address_canton)
            divisionThreeText = stringResource(id = string.credit_address_district)
        }
        CompanyAddressViewModel.ONE -> {
            divisionOneText = stringResource(id = string.credit_address_state)
            divisionTwoText = stringResource(id = string.credit_address_municipality)
            divisionThreeText = stringResource(id = string.credit_address_zone)
        }
        CompanyAddressViewModel.TWO -> {
            divisionOneText = stringResource(id = string.credit_address_state)
            divisionTwoText = stringResource(id = string.credit_address_municipality)
        }
    }

    LaunchedEffect(true) {
        sharedViewModel.onUIEvent(
            CreditViewModel.UIEvent.OnSetNavigation(
                nextAction = {
                    viewModel.onUIEvent(HomeAddressViewModel.UIEvent.OnNextActionClick {
                        sharedViewModel.onUIEvent(CreditViewModel.UIEvent.OnNextStep)
                    })
                },
                nextStep = Six.id, previousStep = Four.id
            )
        )
        viewModel.onUIEvent(HomeAddressViewModel.UIEvent.OnFormValid)
        viewModel.onUIEvent(HomeAddressViewModel.UIEvent.OnCallCatalogs(
            PK_USER,
            USER,
            Brand.CostaRica.id,
            onLoadingValueChange = { isLoading ->
                sharedViewModel.onUIEvent(OnLoadingValueChange(isLoading))
            },
            onFailureWithDialog = { isLoading, dialogParameters ->
                sharedViewModel.onUIEvent(OnFailureWithDialog(isLoading, dialogParameters))
            }
        )
        )
        viewModel.baseEvent.collectLatest { event ->
            when (event) {
                is HomeAddressViewModel.BaseEvent.IsFormCompleted -> sharedViewModel.onUIEvent(OnContinueEnable(event.isCompleted))
            }
        }
    }

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = stringResource(id = string.credit_home_address_title),
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
            onValueChange = { viewModel.onUIEvent(HomeAddressViewModel.UIEvent.OnDivisionOneValueChange(it)) },
            labelText = divisionOneText,
            placeHolder = stringResource(id = string.select)
        )
        CustomDropdown(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            items = viewModel.uiState.divisionTwoList,
            value = viewModel.uiState.divisionTwoSelected,
            onValueChange = { viewModel.onUIEvent(HomeAddressViewModel.UIEvent.OnDivisionTwoValueChange(it)) },
            labelText = divisionTwoText,
            placeHolder = stringResource(id = string.select)
        )
        if (viewModel.country != CompanyAddressViewModel.TWO) {
            CustomDropdown(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                items = viewModel.uiState.divisionThreeList,
                value = viewModel.uiState.divisionThreeSelected,
                onValueChange = { viewModel.onUIEvent(HomeAddressViewModel.UIEvent.OnDivisionThreeValueChange(it)) },
                labelText = divisionThreeText,
                placeHolder = stringResource(id = string.select)
            )
        }
        CustomOutlinedTextField(
            modifier = Modifier.padding(top = 16.dp),
            labelText = stringResource(id = string.credit_address_accurate_address),
            value = viewModel.uiState.address,
            onValueChange = { viewModel.onUIEvent(HomeAddressViewModel.UIEvent.OnAddressValueChange(it)) },
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