package com.multimoney.multimoney.presentation.ui.smart.origination.livingaddress

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
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.credit.origination.homeaddress.HomeAddressViewModel
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel
import com.multimoney.multimoney.presentation.uielement.CustomDropdown
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField

@Composable
fun SmartLivAddressScreen(
    viewModel: SmartLivAddressViewModel = hiltViewModel(),
    sharedViewModel: SmartViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current

    LaunchedEffect(true){
        viewModel.onUIEvent(
            SmartLivAddressViewModel.UIEvent.OnGetUserData(
                pkUser = sharedViewModel.pkUser,
                user = sharedViewModel.user,
                idBrand = sharedViewModel.idBrand.toInt()
            )
        )
    }

    var divisionOneText = ""
    var divisionTwoText = ""
    var divisionThreeText = ""

    when (sharedViewModel.idBrand.toInt()) {
        Brand.CostaRica.id -> {
            divisionOneText = stringResource(id = R.string.credit_address_province)
            divisionTwoText = stringResource(id = R.string.credit_address_canton)
            divisionThreeText = stringResource(id = R.string.credit_address_district)
        }
        Brand.ElSalvador.id -> {
            divisionTwoText = stringResource(id = R.string.credit_address_state)
            divisionThreeText = stringResource(id = R.string.credit_address_municipality)
        }
    }

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = stringResource(id = R.string.smart_liv_address_title),
            modifier = Modifier.padding(top = 16.dp),
            style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.labelText
        )
        if (sharedViewModel.idBrand.toInt() != Brand.ElSalvador.id) {
            CustomDropdown(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
                items = viewModel.uiState.divisionOneList,
                value = viewModel.uiState.divisionOneSelected,
                onValueChange = {
                    viewModel.onUIEvent(
                        SmartLivAddressViewModel.UIEvent.OnDivisionOneValueChange(
                            divisionOne = it,
                            onLoadingValueChange = { isLoading ->
                                sharedViewModel.onUIEvent(
                                    SmartViewModel.UIEvent.OnLoadingValueChange(
                                        isLoading
                                    )
                                )
                            },
                            onFailureWithDialog = { isLoading, dialogParameters ->
                                sharedViewModel.onUIEvent(
                                    SmartViewModel.UIEvent.OnFailureWithDialog(
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
        }
        CustomDropdown(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp),
            items = viewModel.uiState.divisionTwoList,
            value = viewModel.uiState.divisionTwoSelected,
            onValueChange = {
                viewModel.onUIEvent(
                    SmartLivAddressViewModel.UIEvent.OnDivisionTwoValueChange(
                        divisionTwo = it,
                        onLoadingValueChange = { isLoading ->
                            sharedViewModel.onUIEvent(
                                SmartViewModel.UIEvent.OnLoadingValueChange(
                                    isLoading
                                )
                            )
                        },
                        onFailureWithDialog = { isLoading, dialogParameters ->
                            sharedViewModel.onUIEvent(
                                SmartViewModel.UIEvent.OnFailureWithDialog(
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
        CustomDropdown(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp),
            items = viewModel.uiState.divisionThreeList,
            value = viewModel.uiState.divisionThreeSelected,
            onValueChange = {
                viewModel.onUIEvent(
                    SmartLivAddressViewModel.UIEvent.OnDivisionThreeValueChange(
                        divisionThree = it
                    )
                )
            },
            labelText = divisionThreeText,
            placeHolder = stringResource(id = R.string.select)
        )
        CustomOutlinedTextField(
            modifier = Modifier.padding(top = 16.dp),
            labelText = stringResource(id = R.string.credit_address_accurate_address),
            value = viewModel.uiState.address,
            onValueChange = { viewModel.onUIEvent(SmartLivAddressViewModel.UIEvent.OnAddressValueChange(it)) },
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
    }
}