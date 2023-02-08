package com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.Brand
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.SourceIncomeViewModel.UIEvent.OnAddressValueChange
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.SourceIncomeViewModel.UIEvent.OnDivisionOneValueChange
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.SourceIncomeViewModel.UIEvent.OnDivisionThreeValueChange
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.SourceIncomeViewModel.UIEvent.OnDivisionTwoValueChange
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.SourceIncomeViewModel.UIEvent.OnGetUserData
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.SourceIncomeOptionsScreen
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.formalsalariedsv.FormalSalariedSvScreen
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.independentprofessional.IndProfessionalScreen
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.otherincome.OtherIncomeScreen
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.ownbusiness.SmartOwnBusinessSvScreen
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.ownbusinessinpartnership.OwnBusinessInPartnershipScreen
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.ownbusinesspersonaltitle.OwnBusinessTitleScreen
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.retired.SmartRetiredScreen
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.salariedcr.SmartCrSalaryScreen
import com.multimoney.multimoney.presentation.uielement.CustomDropdown
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.util.catalog.SourceIncomeOptionType

/**
 * This is the host screen of options, the content inside should be replaceable for the
 * selected screen. e.g. FreelancerScreen, OwnBusinessScree, etc.
 */
@Composable
fun SourceIncomeScreen(
    viewModel: SourceIncomeViewModel = hiltViewModel(),
    sharedViewModel: SmartViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {
        viewModel.onUIEvent(
            OnGetUserData(
                sharedViewModel.user,
                sharedViewModel.idBrandAsInt,
                sharedViewModel.accountSmartData
            )
        )
    }
    ShowSelectedSourceIncomeOption(
        selectedOption = viewModel.uiState.selectedOption,
        sharedViewModel = sharedViewModel,
        sourceIncomeSharedViewModel = viewModel
    )
}

/**
 * This composable function is intended to replace the composable content depending
 * on the selected option.
 */
@Composable
fun ShowSelectedSourceIncomeOption(
    selectedOption: Int,
    sharedViewModel: SmartViewModel,
    sourceIncomeSharedViewModel: SourceIncomeViewModel
) {
    when (selectedOption) {
        SourceIncomeOptionType.OwnBusiness.id -> SmartOwnBusinessSvScreen(
            sharedViewModel = sharedViewModel,
            sourceIncomeSharedViewModel = sourceIncomeSharedViewModel
        )
        SourceIncomeOptionType.FormalSalariedCr.id -> SmartCrSalaryScreen(
            sharedViewModel = sharedViewModel,
            sourceIncomeSharedViewModel = sourceIncomeSharedViewModel
        )
        SourceIncomeOptionType.Retired.id -> SmartRetiredScreen(
            sharedViewModel = sharedViewModel,
            sourceIncomeSharedViewModel = sourceIncomeSharedViewModel
        )
        SourceIncomeOptionType.OtherSV.id,
        SourceIncomeOptionType.OtherCR.id -> OtherIncomeScreen(
            sharedViewModel = sharedViewModel,
            sourceIncomeSharedViewModel = sourceIncomeSharedViewModel
        )
        SourceIncomeOptionType.OwnBusinessInPartnership.id -> OwnBusinessInPartnershipScreen(
            sharedViewModel = sharedViewModel,
            sourceIncomeSharedViewModel = sourceIncomeSharedViewModel
        )
        SourceIncomeOptionType.FreeLancer.id -> IndProfessionalScreen(
            sharedViewModel = sharedViewModel,
            sourceIncomeSharedViewModel = sourceIncomeSharedViewModel
        )
        SourceIncomeOptionType.OwnBusinessOnPersonalBasis.id -> OwnBusinessTitleScreen(
            sharedViewModel = sharedViewModel,
            sourceIncomeSharedViewModel = sourceIncomeSharedViewModel
        )
        SourceIncomeOptionType.FormalSalariedSv.id -> FormalSalariedSvScreen(
            sharedViewModel = sharedViewModel,
            sourceIncomeSharedViewModel = sourceIncomeSharedViewModel
        )

        else -> {
            // if no option gets selected whenever tapping an item from the list, then it means
            // we should show the main source of income options screen.
            SourceIncomeOptionsScreen(
                sharedViewModel = sharedViewModel,
                sourceIncomeSharedViewModel = sourceIncomeSharedViewModel
            )
        }
    }
}

/**
 * SmartAddressFields: This address fields are common across source income options screens of Smart Origination
 *
 * Parameters:
 * @param sourceIncomeSharedViewModel: ViewModel who will control the fields and make the api calls necessary
 * @param user: String of username
 * @param idBrand: Integer of id brand
 * @param focusManager: Focus manager of the screen where this is called
 */
@Composable
fun SmartAddressFields(
    sourceIncomeSharedViewModel: SourceIncomeViewModel,
    user: String,
    idBrand: Int,
    focusManager: FocusManager
) {
    var divisionOneText = ""
    var divisionTwoText = ""
    var divisionThreeText = ""

    when (idBrand) {
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

    if (idBrand == Brand.CostaRica.id) {
        CustomDropdown(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            items = sourceIncomeSharedViewModel.uiState.divisionOneList?.map { it?.name.orEmpty() }
                ?: listOf(),
            value = sourceIncomeSharedViewModel.uiState.divisionOneSelected?.name ?: "",
            onValueChange = { valueSelected, _ ->
                sourceIncomeSharedViewModel.onUIEvent(
                    OnDivisionOneValueChange(
                        user = user,
                        idBrand = idBrand,
                        divisionOne = valueSelected
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
            .padding(top = 16.dp),
        items = sourceIncomeSharedViewModel.uiState.divisionTwoList?.map { it?.name.orEmpty() }
            ?: listOf(),
        value = sourceIncomeSharedViewModel.uiState.divisionTwoSelected?.name ?: "",
        onValueChange = { valueSelected, _ ->
            sourceIncomeSharedViewModel.onUIEvent(
                OnDivisionTwoValueChange(
                    user = user,
                    idBrand = idBrand,
                    divisionTwo = valueSelected
                )
            )
        },
        labelText = divisionTwoText,
        placeHolder = stringResource(id = R.string.select)
    )

    CustomDropdown(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        items = sourceIncomeSharedViewModel.uiState.divisionThreeList?.map { it?.name.orEmpty() }
            ?: listOf(),
        value = sourceIncomeSharedViewModel.uiState.divisionThreeSelected?.name ?: "",
        onValueChange = { valueSelected, _ ->
            sourceIncomeSharedViewModel.onUIEvent(
                OnDivisionThreeValueChange(
                    user = user,
                    idBrand = idBrand,
                    divisionThree = valueSelected
                )
            )
        },
        labelText = divisionThreeText,
        placeHolder = stringResource(id = R.string.select)
    )

    CustomOutlinedTextField(
        modifier = Modifier.padding(top = 16.dp),
        labelText = stringResource(id = R.string.smart_salaried_working_address),
        value = sourceIncomeSharedViewModel.uiState.address,
        onValueChange = {
            sourceIncomeSharedViewModel.onUIEvent(
                OnAddressValueChange(it)
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
        isRequiredMessage = stringResource(R.string.smart_salaried_working_address_required),
        isError = sourceIncomeSharedViewModel.uiState.addressError.first,
        errorMessage = stringResource(sourceIncomeSharedViewModel.uiState.addressError.second),
        isTextArea = true
    )

    LoadingIndicator(sourceIncomeSharedViewModel.uiState.isLoading)
}
