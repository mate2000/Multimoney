package com.multimoney.multimoney.presentation.ui.credit.origination.creditbank.crosseling

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.CreditStep
import com.multimoney.multimoney.R
import com.multimoney.multimoney.R.drawable
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnCallMutationSaveCreditFlowStep
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnContinueEnable
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnFailure
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnFailureWithDialog
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnLoadingValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.creditbank.crosseling.CreditCrosselingBankViewModel.BaseEvent.OnFormCompleted
import com.multimoney.multimoney.presentation.ui.credit.origination.creditbank.crosseling.CreditCrosselingBankViewModel.UIEvent.OnAccountNumberValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.creditbank.crosseling.CreditCrosselingBankViewModel.UIEvent.OnAccountTypeValueChanged
import com.multimoney.multimoney.presentation.ui.credit.origination.creditbank.crosseling.CreditCrosselingBankViewModel.UIEvent.OnBankValueChanged
import com.multimoney.multimoney.presentation.ui.credit.origination.creditbank.crosseling.CreditCrosselingBankViewModel.UIEvent.OnCallQueryBankList365TypeAccountType
import com.multimoney.multimoney.presentation.ui.credit.origination.creditbank.crosseling.CreditCrosselingBankViewModel.UIEvent.OnCallQueryBanksAndRegularExpression
import com.multimoney.multimoney.presentation.ui.credit.origination.creditbank.crosseling.CreditCrosselingBankViewModel.UIEvent.OnNextActionClick
import com.multimoney.multimoney.presentation.ui.credit.origination.creditbank.crosseling.CreditCrosselingBankViewModel.UIEvent.OnValidateForm
import com.multimoney.multimoney.presentation.uielement.CustomDropdown
import com.multimoney.multimoney.presentation.uielement.CustomInformativeText
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField

@Composable
@Preview
fun CreditBankScreen(
    sharedViewModel: CreditViewModel = hiltViewModel(),
    viewModel: CreditCrosselingBankViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    var title = R.string.empty
    if (sharedViewModel.idBrand.isNotEmpty()) {
        title = when (sharedViewModel.idBrand.toInt()) {
            Brand.CostaRica.id -> R.string.credit_bank_title
            else -> R.string.credit_bank_title_sv

        }
    }

    LaunchedEffect(true) {
        viewModel.baseEvent.collect { event ->
            when (event) {
                is OnFormCompleted -> {
                    sharedViewModel.onUIEvent(OnContinueEnable(event.isFormCompleted))
                }
            }
        }
    }

    LaunchedEffect(true) {
        viewModel.onUIEvent(OnValidateForm)
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
                }, nextStep = if (sharedViewModel.crosseling) {
                    if (sharedViewModel.idBrand.toInt() == Brand.ElSalvador.id) {
                        CreditStep.Three.id
                    } else {
                        CreditStep.Four.id
                    }
                } else {
                    CreditStep.Three.id
                }, previousStep = CreditStep.One.id
            )
        )
        viewModel.onUIEvent(
            OnCallQueryBankList365TypeAccountType(
                sharedViewModel.email,
                sharedViewModel.idBrand.toInt(),
                onLoadingValueChange = { isLoading ->
                    sharedViewModel.onUIEvent(OnLoadingValueChange(isLoading))
                },
                onFailureWithDialog = { isLoading, dialogParameter ->
                    sharedViewModel.onUIEvent(OnFailureWithDialog(isLoading, dialogParameter))
                }
            )
        )
        viewModel.onUIEvent(
            OnCallQueryBanksAndRegularExpression(
                pkUser = sharedViewModel.pkUser.toInt(),
                user = sharedViewModel.email,
                idBrand = sharedViewModel.idBrand.toInt(),
                idUserRequest = sharedViewModel.idUserRequest,
                onFailureWithDialog = {
                    sharedViewModel.onUIEvent(OnFailure(it))
                }
            )
        )
    }

    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp)
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            text = stringResource(id = title),
            style = Typography.h6.copy(
                color = MultimoneyTheme.colors.titleText,
                fontWeight = FontWeight.SemiBold
            )
        )

        CustomInformativeText(
            modifier = Modifier.padding(top = 24.dp),
            leadingIcon = drawable.ic_information,
            text = stringResource(id = string.credit_bank_condition),
            textStyle = Typography.body2.copy(color = MultimoneyTheme.colors.labelText)
        )

        CustomDropdown(
            modifier = Modifier
                .wrapContentSize(Alignment.TopStart)
                .focusable(false)
                .padding(top = 32.dp),
            items = viewModel.uiState.bankList?.map { it?.bankName ?: "" } ?: listOf(),
            onValueChange = { _, index ->
                viewModel.onUIEvent(OnBankValueChanged(viewModel.uiState.bankList?.get(index)))
            },
            labelText = stringResource(id = R.string.credit_bank_account_destiny),
            value = viewModel.uiState.bankSelectedString,
            placeHolder = stringResource(id = R.string.credit_bank_select)
        )

        CustomDropdown(
            modifier = Modifier
                .wrapContentSize(Alignment.TopStart)
                .focusable(false)
                .padding(top = 16.dp),
            items = viewModel.uiState.accountTypeList?.map { it?.typeName ?: "" } ?: listOf(),
            onValueChange = { _, index ->
                viewModel.onUIEvent(
                    OnAccountTypeValueChanged(
                        viewModel.uiState.accountTypeList?.get(index)
                    )
                )
            },
            labelText = stringResource(id = R.string.credit_bank_account_type),
            value = viewModel.uiState.accountTypeSelectedString,
            placeHolder = stringResource(id = R.string.credit_bank_select)
        )

        CustomOutlinedTextField(
            modifier = Modifier.padding(top = 16.dp),
            value = viewModel.uiState.accountNumber,
            leadingIcon = R.drawable.ic_account,
            placeHolder = stringResource(R.string.credit_bank_account_number_hint),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            }),
            labelText = stringResource(id = R.string.credit_bank_account_number_label),
            isRequiredMessage = stringResource(id = R.string.credit_bank_account_number_required),
            onValueChange = {
                viewModel.onUIEvent(
                    OnAccountNumberValueChange(
                        it
                    )
                )
            },
            isError = viewModel.uiState.accountNumberError.first,
            errorMessage = stringResource(id = viewModel.uiState.accountNumberError.second)
        )
    }
}
