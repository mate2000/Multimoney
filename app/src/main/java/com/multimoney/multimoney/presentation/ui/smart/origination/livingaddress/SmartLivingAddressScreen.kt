package com.multimoney.multimoney.presentation.ui.smart.origination.livingaddress

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import com.multimoney.data.util.catalog.SmartSteps
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.CustomDropdown
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField

@Composable
fun SmartLivingAddressScreen(
    viewModel: SmartLivAddressViewModel = hiltViewModel(),
    sharedViewModel: SmartViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    LaunchedEffect(true) {
        sharedViewModel.onUIEvent(SmartViewModel.UIEvent.OnContinueVisible(true))
        sharedViewModel.onUIEvent(SmartViewModel.UIEvent.OnContinueEnable(viewModel.isFormValid()))

        viewModel.onUIEvent(
            SmartLivAddressViewModel.UIEvent.OnGetUserData(
                accountSmartData = sharedViewModel.accountSmartData,
                user = sharedViewModel.user,
                idBrand = sharedViewModel.idBrandAsInt
            )
        )

        sharedViewModel.onUIEvent(
            SmartViewModel.UIEvent.OnSetNavigation(
                nextAction = {
                    sharedViewModel.onUIEvent(
                        SmartViewModel.UIEvent.OnCallMutationUpdateGlobalRequestUseCase(
                            accountSmartData = sharedViewModel.accountSmartData?.copy(
                                idAddressLevel1 = viewModel.uiState.divisionOneSelected?.id?.toLong()
                                    ?: 0,
                                idAddressLevel2 = viewModel.uiState.divisionTwoSelected?.id?.toLong()
                                    ?: 0,
                                idAddressLevel3 = viewModel.uiState.divisionThreeSelected?.id?.toLong()
                                    ?: 0,
                                strAddressLevel1 = viewModel.uiState.divisionOneSelected?.name,
                                strAddressLevel2 = viewModel.uiState.divisionTwoSelected?.name,
                                strAddressLevel3 = viewModel.uiState.divisionThreeSelected?.name,
                                addressDetail = viewModel.uiState.address,
                                currentStep = SmartSteps.Search.getNameById(
                                    sharedViewModel.uiState.currentStep
                                )
                            )
                        )
                    )
                },
                nextStep = if (viewModel.idBrand == Brand.ElSalvador.id) SmartSteps.Three.id else SmartSteps.Two.id,
                previousStep = SmartSteps.One.id
            )
        )
        viewModel.baseEvent.collect { event ->
            when (event) {
                is SmartLivAddressViewModel.BaseEvent.OnFormValidateCompleted -> sharedViewModel.onUIEvent(
                    SmartViewModel.UIEvent.OnContinueEnable(event.isFormValid)
                )
            }
        }
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

    if (viewModel.uiState.openDialog.isActive.value) {
        CustomDialog(
            title = stringResource(id = viewModel.uiState.openDialog.titleResource),
            message = viewModel.uiState.openDialog.description.ifBlank {
                stringResource(R.string.error)
            },
            onPositiveAction = viewModel.uiState.openDialog.positiveAction
        )
    }

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = stringResource(id = R.string.smart_liv_address_title),
            modifier = Modifier.padding(top = 16.dp),
            style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.labelText
        )
        if (sharedViewModel.idBrandAsInt != Brand.ElSalvador.id) {
            CustomDropdown(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
                items = viewModel.uiState.divisionOneList?.map { it?.name.orEmpty() } ?: listOf(),
                value = viewModel.uiState.divisionOneSelected?.name ?: "",
                onValueChange = {
                    viewModel.onUIEvent(
                        SmartLivAddressViewModel.UIEvent.OnDivisionOneValueChange(
                            divisionOne = it,
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
        } else {
            viewModel.onUIEvent(
                SmartLivAddressViewModel.UIEvent.OnNotApplicable(
                    stringResource(id = R.string.not_applicable)
                )
            )
        }
        CustomDropdown(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp),
            items = viewModel.uiState.divisionTwoList?.map { it?.name.orEmpty() } ?: listOf(),
            value = viewModel.uiState.divisionTwoSelected?.name ?: "",
            onValueChange = {
                viewModel.onUIEvent(
                    SmartLivAddressViewModel.UIEvent.OnDivisionTwoValueChange(
                        divisionTwo = it,
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
            items = viewModel.uiState.divisionThreeList?.map { it?.name.orEmpty() } ?: listOf(),
            value = viewModel.uiState.divisionThreeSelected?.name ?: "",
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
            onValueChange = {
                viewModel.onUIEvent(
                    SmartLivAddressViewModel.UIEvent.OnAddressValueChange(
                        it
                    )
                )
            },
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
            isRequiredMessage = stringResource(R.string.credit_company_address_accurate_address_error),
            isError = viewModel.uiState.addressError.first,
            errorMessage = stringResource(viewModel.uiState.addressError.second),
            isTextArea = true
        )
    }
}
