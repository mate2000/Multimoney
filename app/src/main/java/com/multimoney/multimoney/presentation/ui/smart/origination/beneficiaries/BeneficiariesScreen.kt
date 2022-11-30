package com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.SmartSteps
import com.multimoney.data.util.catalog.SmartSteps.Five
import com.multimoney.data.util.catalog.SmartSteps.Three
import com.multimoney.domain.model.accountsmart.Beneficiary
import com.multimoney.multimoney.R
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnCallMutationUpdateGlobalRequestUseCase
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnClickBottomSheet
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnContinueEnable
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnSetNavigation
import com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries.BeneficiariesViewModel.BaseEvent.OnFormValidateCompleted
import com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries.BeneficiariesViewModel.Companion.MAX_PERCENTAGE
import com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries.BeneficiariesViewModel.UIEvent
import com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries.BeneficiariesViewModel.UIEvent.OnAddBeneficiaryOptionChange
import com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries.BeneficiariesViewModel.UIEvent.OnAddBeneficiaryStateChange
import com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries.BeneficiariesViewModel.UIEvent.OnBeneficiaryFullNameValueChange
import com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries.BeneficiariesViewModel.UIEvent.OnCallQueryRelationshipUseCase
import com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries.BeneficiariesViewModel.UIEvent.OnEditBeneficiaryClick
import com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries.BeneficiariesViewModel.UIEvent.OnPercentageValueChange
import com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries.BeneficiariesViewModel.UIEvent.OnRelationshipValueChange
import com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries.BeneficiariesViewModel.UIEvent.OnRemoveBeneficiaryClick
import com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries.BeneficiariesViewModel.UIEvent.OnValidateForm
import com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries.edit.EditBeneficiaryBottomSheet
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.CustomDropdown
import com.multimoney.multimoney.presentation.uielement.CustomInfoButton
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField

@Composable
fun BeneficiariesScreen(
    beneficiaryViewModel: BeneficiariesViewModel = hiltViewModel(),
    sharedViewModel: SmartViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = true) {
        sharedViewModel.onUIEvent(SmartViewModel.UIEvent.OnContinueVisible(true))
        beneficiaryViewModel.baseEvent.collect { event ->
            when (event) {
                is OnFormValidateCompleted -> sharedViewModel.onUIEvent(
                    OnContinueEnable(event.isFormValid)
                )
            }
        }
    }

    LaunchedEffect(true) {
        beneficiaryViewModel.onUIEvent(
            OnCallQueryRelationshipUseCase(
                sharedViewModel.user,
                sharedViewModel.idBrandAsInt,
                0
            )
        )
        sharedViewModel.onUIEvent(
            OnSetNavigation(
                nextAction = {
                    if (beneficiaryViewModel.uiState.addBeneficiaryState) {
                        beneficiaryViewModel.onUIEvent(
                            OnAddBeneficiaryStateChange(
                                false,
                                Beneficiary(
                                    beneficiaryViewModel.uiState.beneficiaryFullName,
                                    beneficiaryViewModel.uiState.relationshipList.find { it?.description == beneficiaryViewModel.uiState.relationship }?.relationshipId,
                                    beneficiaryViewModel.uiState.relationship,
                                    beneficiaryViewModel.uiState.percentage
                                )
                            )
                        )
                    } else {
                        beneficiaryViewModel.onUIEvent(
                            UIEvent.OnNextActionClick(
                                nextStepAction = {
                                    sharedViewModel.onUIEvent(
                                        OnCallMutationUpdateGlobalRequestUseCase(
                                            accountSmartData = sharedViewModel.accountSmartData?.copy(
                                                listBeneficiaries = beneficiaryViewModel.uiState.beneficiaryList,
                                                currentStep = SmartSteps.Search.getNameById(
                                                    sharedViewModel.uiState.currentStep
                                                )
                                            )
                                        )
                                    )
                                }
                            )
                        )
                    }
                },
                nextStep = Five.id,
                previousStep = Three.id
            )
        )
        beneficiaryViewModel.onUIEvent(OnValidateForm)
    }

    val generalModifier = if (beneficiaryViewModel.uiState.addBeneficiaryState) {
        Modifier
            .background(MultimoneyTheme.colors.background)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    } else {
        Modifier
            .background(MultimoneyTheme.colors.background)
            .fillMaxSize()
    }

    Column(
        modifier = generalModifier
    ) {
        if (beneficiaryViewModel.uiState.addBeneficiaryState) BeneficiaryForm(beneficiaryViewModel)
        else BeneficiaryList(beneficiaryViewModel, sharedViewModel)
    }

    if (beneficiaryViewModel.uiState.openDialog.isActive.value) {
        CustomDialog(
            title = stringResource(id = beneficiaryViewModel.uiState.openDialog.titleResource),
            message = stringResource(id = beneficiaryViewModel.uiState.openDialog.descriptionResource),
            negativeButtonText = stringResource(id = beneficiaryViewModel.uiState.openDialog.negativeResource),
            positiveButtonText = stringResource(id = beneficiaryViewModel.uiState.openDialog.positiveResource),
            openDialogCustom = beneficiaryViewModel.uiState.openDialog.isActive,
            onPositiveAction = beneficiaryViewModel.uiState.openDialog.positiveAction,
        )
    }

    BackHandler {
        beneficiaryViewModel.onUIEvent(OnAddBeneficiaryOptionChange(false))
    }
}

@Composable
fun BeneficiaryForm(viewModel: BeneficiariesViewModel) {
    val focusManager = LocalFocusManager.current
    CustomOutlinedTextField(
        value = viewModel.uiState.beneficiaryFullName,
        onValueChange = { beneficiaryName ->
            viewModel.onUIEvent(OnBeneficiaryFullNameValueChange(beneficiaryName))
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(onDone = {
            focusManager.clearFocus()
        }),
        labelText = stringResource(id = string.sing_up_personal_data_cr_complete_name),
        modifier = Modifier.padding(top = 24.dp)
    )

    CustomDropdown(
        modifier = Modifier
            .padding(top = 16.dp)
            .wrapContentSize(Alignment.TopStart)
            .focusable(false),
        items = viewModel.uiState.relationshipList.map { relationshipStatus ->
            relationshipStatus?.description ?: ""
        },
        value = viewModel.uiState.relationship,
        onValueChange = { viewModel.onUIEvent(OnRelationshipValueChange(it)) },
        labelText = stringResource(id = string.relationship),
        placeHolder = stringResource(id = string.select)
    )

    CustomOutlinedTextField(
        value = viewModel.uiState.percentage,
        placeHolder = stringResource(id = string.into),
        onValueChange = { percentage ->
            viewModel.onUIEvent(OnPercentageValueChange(percentage))
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = {
            focusManager.clearFocus()
        }),
        labelText = stringResource(id = string.smart_account_beneficiaries_percentage),
        modifier = Modifier.padding(top = 16.dp)
    )
    viewModel.onUIEvent(OnValidateForm)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BeneficiaryList(
    viewModel: BeneficiariesViewModel = hiltViewModel(),
    sharedViewModel: SmartViewModel
) {
    OnContinueEnable(true)
    viewModel.uiState.beneficiaryList.let { beneficiaries ->
        LazyColumn(modifier = Modifier.padding(top = 20.dp, start = 16.dp, end = 16.dp)) {
            items(beneficiaries) { beneficiary ->
                CustomInfoButton(
                    title = beneficiary.fullName ?: "",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    subtitle = stringResource(
                        id = string.smart_account_beneficiary_content,
                        beneficiary.strRelationship ?: "",
                        beneficiary.allocationPercentage ?: ""
                    ),
                    endIcon = R.drawable.ic_options,
                    startIcon = R.drawable.ic_beneficiary,
                    onEndIconClick = {
                        sharedViewModel.onUIEvent(OnClickBottomSheet)
                        sharedViewModel.uiState.bottomSheet = {
                            EditBeneficiaryBottomSheet(
                                coroutineScope = rememberCoroutineScope(),
                                modalBottomSheetState = sharedViewModel.uiState.bottomSheetState,
                                onEditClick = {
                                    viewModel.onUIEvent(OnEditBeneficiaryClick(beneficiary))
                                },
                                onRemoveClick = {
                                    viewModel.onUIEvent(OnRemoveBeneficiaryClick(beneficiary))
                                },
                                onBackClick = {
                                    if (sharedViewModel.uiState.bottomSheetState.isVisible) {
                                        sharedViewModel.onUIEvent(OnClickBottomSheet)
                                    }
                                }
                            )
                        }
                    }
                )
            }
        }
    }
    if (viewModel.uiState.totalPercentage < MAX_PERCENTAGE) {
        CustomButton(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, bottom = 32.dp, top = 16.dp)
                .fillMaxWidth()
                .height(48.dp),
            buttonType = CustomButtonType.PrimaryTertiary,
            text = stringResource(id = string.smart_account_add_beneficiaries),
            onClick = {
                viewModel.onUIEvent(OnAddBeneficiaryStateChange(true))
            }
        )
    }
}
