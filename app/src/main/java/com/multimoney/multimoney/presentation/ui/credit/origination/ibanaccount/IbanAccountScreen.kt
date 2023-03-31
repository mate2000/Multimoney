package com.multimoney.multimoney.presentation.ui.credit.origination.ibanaccount

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
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
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnCallMutationSaveCreditFlowStep
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnFailureWithDialog
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnLoadingValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.ibanaccount.IbanAccountViewModel.BaseEvent.OnFormValidateCompleted
import com.multimoney.multimoney.presentation.ui.credit.origination.ibanaccount.IbanAccountViewModel.UIEvent.OnAccountValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.ibanaccount.IbanAccountViewModel.UIEvent.OnLoadCreditSteps
import com.multimoney.multimoney.presentation.ui.credit.origination.ibanaccount.IbanAccountViewModel.UIEvent.OnNextActionClick
import com.multimoney.multimoney.presentation.ui.credit.origination.ibanaccount.IbanAccountViewModel.UIEvent.OnNextActionClickCrosseling
import com.multimoney.multimoney.presentation.ui.credit.origination.ibanaccount.IbanAccountViewModel.UIEvent.OnOpenInformativeDialog
import com.multimoney.multimoney.presentation.ui.credit.origination.ibanaccount.IbanAccountViewModel.UIEvent.OnUpdateUserInfo
import com.multimoney.multimoney.presentation.ui.credit.origination.ibanaccount.IbanAccountViewModel.UIEvent.OnValidForm
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.CustomInfoButton
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.util.catalog.AdjustEventType
import com.multimoney.multimoney.presentation.util.getMaskedAccount
import com.multimoney.multimoney.presentation.util.transformation.MaskVisualTransformation
import com.multimoney.multimoney.presentation.util.transformation.VisualTransformationMasks

@Composable
fun IbanAccountScreen(
    sharedViewModel: CreditViewModel,
    viewModel: IbanAccountViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current

    LaunchedEffect(true) {
        viewModel.baseEvent.collect { event ->
            when (event) {
                is OnFormValidateCompleted -> sharedViewModel.onUIEvent(
                    CreditViewModel.UIEvent.OnContinueEnable(
                        event.isFormValid
                    )
                )
            }
        }
    }

    LaunchedEffect(true) {
        viewModel.onUIEvent(
            OnUpdateUserInfo(
                sharedViewModel.identification,
                sharedViewModel.email,
                sharedViewModel.idBrand
            )
        )
        viewModel.onUIEvent(OnValidForm)
        sharedViewModel.onUIEvent(
            CreditViewModel.UIEvent.OnSetNavigation(
                nextAction = {
                    viewModel.onUIEvent(
                        if (sharedViewModel.crosseling) {
                            OnNextActionClickCrosseling(
                                user = sharedViewModel.email,
                                nextStepAction = {
                                    sharedViewModel.onUIEvent(OnCallMutationSaveCreditFlowStep)
                                },
                                saveCreditStepsHelper = sharedViewModel.saveCreditStepsHelper,
                                onLoadingValueChange = { isLoading ->
                                    sharedViewModel.onUIEvent(OnLoadingValueChange(isLoading))
                                },
                                onFailureWithDialog = { isLoading, dialogParameters ->
                                    sharedViewModel.onUIEvent(OnFailureWithDialog(isLoading, dialogParameters))
                                }
                            )
                        } else {
                            OnNextActionClick(
                                user = sharedViewModel.email,
                                nextStepAction = {
                                    sharedViewModel.onUIEvent(OnCallMutationSaveCreditFlowStep)
                                },
                                saveCreditStepsHelper = sharedViewModel.saveCreditStepsHelper
                            )
                        }
                    )
                    sharedViewModel.logEvents(
                        AdjustEventType.ORIGINATION_FIRST_FILL_ACCOUNT_5004
                    )
                },
                nextStep = if (sharedViewModel.crosseling) {
                    if (sharedViewModel.idBrand.toInt() == Brand.ElSalvador.id) {
                        CreditStep.Three.id
                    } else {
                        CreditStep.Four.id
                    }
                } else {
                    CreditStep.Three.id
                },
                previousStep = CreditStep.One.id
            )
        )
        if (sharedViewModel.crosseling.not()) {
            viewModel.onUIEvent(
                OnLoadCreditSteps(
                    list = sharedViewModel.saveCreditStepsHelper.inputTextInfoList,
                    onFailureWithDialog = { isLoading, dialogParameters ->
                        sharedViewModel.onUIEvent(
                            OnFailureWithDialog(
                                isLoading,
                                dialogParameters
                            )
                        )
                    }
                )
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MultimoneyTheme.colors.background)
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.iban_account_tile),
            modifier = Modifier.padding(top = 8.dp),
            style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.titleText
        )

        if (viewModel.uiState.ibanSuccess) {
            CustomInfoButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                startIcon = R.drawable.ic_bank_account,
                title = viewModel.validateAccount?.bankName ?: "",
                subtitle = getMaskedAccount(
                    accountNumber = viewModel.uiState.accountNumber,
                    maskedText = stringResource(id = R.string.payment_account_masked_text),
                    prefix = if (sharedViewModel.idBrand.toInt() == Brand.CostaRica.id) {
                        Brand.CostaRica.iban
                    } else {
                        Brand.Default.iban
                    }
                ),
                endIcon = R.drawable.ic_edit_green,
                onEndIconClick = {
                    viewModel.onUIEvent(OnOpenInformativeDialog)
                }
            )
        } else {
            CustomOutlinedTextField(
                modifier = Modifier.padding(top = 28.dp),
                value = viewModel.uiState.accountNumber,
                leadingIconComposable = { tint ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 16.dp, end = 8.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_account),
                            contentDescription = "",
                            tint = tint
                        )
                        Text(
                            modifier = Modifier.padding(start = 8.dp),
                            text = stringResource(id = R.string.iban_account_cr),
                            style = Typography.body2.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = tint
                            )
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.clearFocus()
                }),
                isRequired = false,
                placeHolder = stringResource(id = R.string.iban_account_hint),
                onValueChange = {
                    viewModel.onUIEvent(
                        OnAccountValueChange(
                            it,
                            onFailureWithDialog = { isLoading, dialogParameters ->
                                sharedViewModel.onUIEvent(
                                    OnFailureWithDialog(
                                        isLoading,
                                        dialogParameters
                                    )
                                )
                            }
                        )
                    )
                },
                canShowNonErrorMessage = true,
                isError = viewModel.uiState.accountError.first,
                errorMessage = viewModel.uiState.validationError
                    ?: stringResource(id = viewModel.uiState.accountError.second),
                customTransformation = MaskVisualTransformation(
                    VisualTransformationMasks.IBAN_TRANSFORMATION_MASK.mask,
                    VisualTransformationMasks.IBAN_TRANSFORMATION_MASK.maskChar
                )
            )
        }
    }
    if (viewModel.uiState.dialogParameters.isActive.value) {
        CustomDialog(
            message = stringResource(id = viewModel.uiState.dialogParameters.descriptionResource),
            openDialogCustom = viewModel.uiState.dialogParameters.isActive,
            onPositiveAction = viewModel.uiState.dialogParameters.positiveAction
        )
    }
}
