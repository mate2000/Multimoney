package com.multimoney.multimoney.presentation.ui.credit.companyaddress

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.credit.companyaddress.CompanyAddressViewModel.Companion.ONE
import com.multimoney.multimoney.presentation.ui.credit.companyaddress.CompanyAddressViewModel.Companion.TWO
import com.multimoney.multimoney.presentation.ui.home.ZERO
import com.multimoney.multimoney.presentation.uielement.CustomDropdown
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField

@Composable
fun CompanyAddressScreen(viewModel: CompanyAddressViewModel = hiltViewModel()) {

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

    Column {
        Text(
            text = stringResource(id = R.string.credit_company_address_title),
            style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.labelText
        )
        CustomDropdown(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp),
            items = listOf(),
            value = viewModel.uiState.divisionOne,
            onValueChange = { viewModel.onUiEvent(CompanyAddressViewModel.UIEvent.OnDivisionOneChange(it)) },
            labelText = divisionOneText,
            placeHolder = stringResource(id = R.string.select)
        )
        CustomDropdown(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            items = listOf(),
            value = viewModel.uiState.divisionTwo,
            onValueChange = { viewModel.onUiEvent(CompanyAddressViewModel.UIEvent.OnDivisionOneChange(it)) },
            labelText = divisionTwoText,
            placeHolder = stringResource(id = R.string.select)
        )
        CustomDropdown(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            items = listOf(),
            value = viewModel.uiState.divisionThree,
            onValueChange = { viewModel.onUiEvent(CompanyAddressViewModel.UIEvent.OnDivisionOneChange(it)) },
            labelText = divisionThreeText,
            placeHolder = stringResource(id = R.string.select)
        )
        CustomOutlinedTextField(
            modifier = Modifier.padding(16.dp),
            labelText = stringResource(id = R.string.credit_company_address_accurate_address),
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