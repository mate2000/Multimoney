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
import com.multimoney.data.util.catalog.CreditStep
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.credit.CreditViewModel
import com.multimoney.multimoney.presentation.ui.credit.companyaddress.CompanyAddressViewModel.BaseEvent.IsFormCompleted
import com.multimoney.multimoney.presentation.ui.credit.companyaddress.CompanyAddressViewModel.Companion.ONE
import com.multimoney.multimoney.presentation.ui.credit.companyaddress.CompanyAddressViewModel.Companion.TWO
import com.multimoney.multimoney.presentation.ui.credit.companyaddress.CompanyAddressViewModel.Companion.ZERO
import com.multimoney.multimoney.presentation.uielement.CustomDropdown
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CompanyAddressScreen(
    sharedViewModel: CreditViewModel,
    viewModel: CompanyAddressViewModel = hiltViewModel()
) {

    val focusManager = LocalFocusManager.current

    var divisionOneText = ""
    var divisionTwoText = ""
    var divisionThreeText = ""

    when (viewModel.country) {
        ZERO -> {
            divisionOneText = stringResource(id = R.string.credit_company_address_province)
            divisionTwoText = stringResource(id = R.string.credit_company_address_canton)
            divisionThreeText = stringResource(id = R.string.credit_company_address_district)
        }
        ONE -> {
            divisionOneText = stringResource(id = R.string.credit_company_address_state)
            divisionTwoText = stringResource(id = R.string.credit_company_address_municipality)
            divisionThreeText = stringResource(id = R.string.credit_company_address_zone)
        }
        TWO -> {
            divisionOneText = stringResource(id = R.string.credit_company_address_state)
            divisionTwoText = stringResource(id = R.string.credit_company_address_municipality)
        }
    }

    LaunchedEffect(true) {
        sharedViewModel.onUIEvent(
            CreditViewModel.UIEvent.OnSetNavigation(
                nextAction = {
                    viewModel.onUIEvent(CompanyAddressViewModel.UIEvent.OnNextActionClick {
                        sharedViewModel.onUIEvent(CreditViewModel.UIEvent.OnNextStep)
                    })
                },
                nextStep = CreditStep.Five.id, previousStep = CreditStep.Three.id
            )
        )
        viewModel.onUIEvent(CompanyAddressViewModel.UIEvent.OnFormValid)
        viewModel.onUIEvent(
            CompanyAddressViewModel.UIEvent.OnCallCatalogs(
                PK_USER,
                USER,
                Brand.Revamp.id,
                onLoadingValueChange = { isLoading ->
                    sharedViewModel.onUIEvent(CreditViewModel.UIEvent.OnLoadingValueChange(isLoading))
                },
                onFailureWithDialog = { isLoading, dialogParameters ->
                    sharedViewModel.onUIEvent(CreditViewModel.UIEvent.OnFailureWithDialog(isLoading, dialogParameters))
                }
            )
        )
        viewModel.baseEvent.collectLatest { event ->
            when (event) {
                is IsFormCompleted -> sharedViewModel.onUIEvent(CreditViewModel.UIEvent.OnContinueEnable(event.isCompleted))
            }
        }
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
            onValueChange = { viewModel.onUIEvent(CompanyAddressViewModel.UIEvent.OnDivisionOneValueChange(it)) },
            labelText = divisionOneText,
            placeHolder = stringResource(id = R.string.select)
        )
        CustomDropdown(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            items = viewModel.uiState.divisionTwoList,
            value = viewModel.uiState.divisionTwoSelected,
            onValueChange = { viewModel.onUIEvent(CompanyAddressViewModel.UIEvent.OnDivisionTwoValueChange(it)) },
            labelText = divisionTwoText,
            placeHolder = stringResource(id = R.string.select)
        )
        if (viewModel.country != TWO) {
            CustomDropdown(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                items = viewModel.uiState.divisionThreeList,
                value = viewModel.uiState.divisionThreeSelected,
                onValueChange = { viewModel.onUIEvent(CompanyAddressViewModel.UIEvent.OnDivisionThreeValueChange(it)) },
                labelText = divisionThreeText,
                placeHolder = stringResource(id = R.string.select)
            )
        }
        CustomOutlinedTextField(
            modifier = Modifier.padding(top = 16.dp),
            labelText = stringResource(id = R.string.credit_company_address_accurate_address),
            value = viewModel.uiState.address,
            onValueChange = { viewModel.onUIEvent(CompanyAddressViewModel.UIEvent.OnAddressValueChange(it)) },
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

const val PK_USER = "229913"
const val USER = "Diego"