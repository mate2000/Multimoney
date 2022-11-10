package com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries

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
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.SmartSteps
import com.multimoney.data.util.catalog.SmartSteps.Five
import com.multimoney.data.util.catalog.SmartSteps.One
import com.multimoney.data.util.catalog.SmartSteps.Three
import com.multimoney.domain.model.accountsmart.Beneficiary
import com.multimoney.multimoney.R
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnCallMutationUpdateGlobalRequestUseCase
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnContinueEnable
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnSetNavigation
import com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries.BeneficiariesViewModel.BaseEvent.OnFormValidateCompleted
import com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries.BeneficiariesViewModel.Companion.MAX_PERCENTAGE
import com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries.BeneficiariesViewModel.UIEvent
import com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries.BeneficiariesViewModel.UIEvent.OnAddBeneficiaryStateChange
import com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries.BeneficiariesViewModel.UIEvent.OnBeneficiaryFullNameValueChange
import com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries.BeneficiariesViewModel.UIEvent.OnCallQueryRelationshipUseCase
import com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries.BeneficiariesViewModel.UIEvent.OnPercentageValueChange
import com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries.BeneficiariesViewModel.UIEvent.OnRelationshipValueChange
import com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries.BeneficiariesViewModel.UIEvent.OnValidateForm
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.CustomDropdown
import com.multimoney.multimoney.presentation.uielement.CustomInfoButton
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField

@Composable
fun BeneficiariesScreen(
    viewModel: BeneficiariesViewModel = hiltViewModel(),
    sharedViewModel: SmartViewModel = hiltViewModel(),
) {

    LaunchedEffect(key1 = true) {
        sharedViewModel.onUIEvent(SmartViewModel.UIEvent.OnContinueVisible(true))
        viewModel.baseEvent.collect { event ->
            when (event) {
                is OnFormValidateCompleted -> sharedViewModel.onUIEvent(
                    OnContinueEnable(event.isFormValid)
                )
            }
        }
    }

    LaunchedEffect(true) {
        viewModel.onUIEvent(
            OnCallQueryRelationshipUseCase(
                sharedViewModel.user,
                sharedViewModel.idBrand.toInt(),
                0
            )
        )
        sharedViewModel.onUIEvent(
            OnSetNavigation(
                nextAction = {
                    if (viewModel.uiState.addBeneficiaryState) {
                        viewModel.onUIEvent(
                            OnAddBeneficiaryStateChange(
                                false, Beneficiary(
                                    viewModel.uiState.beneficiaryFullName,
                                    viewModel.uiState.relationshipList.find { it?.description == viewModel.uiState.relationship }?.relationshipId,
                                    viewModel.uiState.relationship,
                                    viewModel.uiState.percentage
                                )
                            )
                        )
                    } else {
                        viewModel.onUIEvent(
                            UIEvent.OnNextActionClick(
                                nextStepAction = {
                                    sharedViewModel.onUIEvent(
                                        OnCallMutationUpdateGlobalRequestUseCase(
                                            accountSmartData = sharedViewModel.accountSmartData?.copy(
                                                listBeneficiaries = viewModel.uiState.beneficiaryList,
                                                currentStep = SmartSteps.Search.getNameById(
                                                    sharedViewModel.uiState.currentStep
                                                )
                                            )
                                        )
                                    )
                                }
                            ))
                    }
                },
                nextStep = Five.id,
                previousStep = Three.id
            )
        )
        viewModel.onUIEvent(OnValidateForm)
    }

    val generalModifier = if (viewModel.uiState.addBeneficiaryState) {
        Modifier
            .padding(vertical = 16.dp, horizontal = 16.dp)
            .background(MultimoneyTheme.colors.background)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    } else {
        Modifier
            .padding(vertical = 16.dp, horizontal = 16.dp)
            .background(MultimoneyTheme.colors.background)
            .fillMaxSize()
    }

    Column(
        modifier = generalModifier
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = Typography.h4.toSpanStyle()
                        .copy(
                            color = MultimoneyTheme.colors.text,
                            fontWeight = FontWeight.SemiBold
                        )
                ) {
                    append(stringResource(id = string.smart_account_beneficiaries_title))
                }
            },
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )
        if (viewModel.uiState.addBeneficiaryState) BeneficiaryForm(viewModel)
        else BeneficiaryList(viewModel)
    }
}

@Composable
fun BeneficiaryForm(viewModel: BeneficiariesViewModel) {
    val focusManager = LocalFocusManager.current
    CustomOutlinedTextField(
        value = viewModel.uiState.beneficiaryFullName,
        placeHolder = "",
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
        modifier = Modifier.padding(top = 44.dp),
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
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = {
            focusManager.clearFocus()
        }),
        labelText = stringResource(id = string.smart_account_beneficiaries_percentage),
        modifier = Modifier.padding(top = 16.dp),
    )

    if (viewModel.uiState.openDialog.isActive.value) {
        CustomDialog(
            title = stringResource(id = viewModel.uiState.openDialog.titleResource),
            message = stringResource(id = viewModel.uiState.openDialog.descriptionResource),
            positiveButtonText = stringResource(id = viewModel.uiState.openDialog.positiveResource),
            openDialogCustom = viewModel.uiState.openDialog.isActive,
            onPositiveAction = viewModel.uiState.openDialog.positiveAction
        )
    }
}

@Composable
fun BeneficiaryList(
    viewModel: BeneficiariesViewModel = hiltViewModel()
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
                        // TODO Implement bottom sheet
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
                .height(48.dp), buttonType = CustomButtonType.PrimaryTertiary,
            text = stringResource(id = string.smart_account_add_beneficiaries),
            onClick = {
                viewModel.onUIEvent(OnAddBeneficiaryStateChange(true))
            }
        )
    }
}