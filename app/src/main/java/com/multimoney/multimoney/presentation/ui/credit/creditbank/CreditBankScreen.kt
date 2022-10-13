package com.multimoney.multimoney.presentation.ui.credit.creditbank

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.credit.CreditViewModel
import com.multimoney.multimoney.presentation.ui.credit.CreditViewModel.UIEvent.OnContinueEnable
import com.multimoney.multimoney.presentation.ui.credit.creditbank.CreditBankViewModel.BaseEvent.OnFormCompleted
import com.multimoney.multimoney.presentation.uielement.CustomDropdown
import com.multimoney.multimoney.presentation.uielement.CustomImage
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField

@Composable
@Preview
fun CreditBankScreen(
    sharedViewModel: CreditViewModel = hiltViewModel(),
    viewModel: CreditBankViewModel = hiltViewModel()
) {

    val focusManager = LocalFocusManager.current
    var title = R.string.empty
    if (sharedViewModel.idBrand.isNotEmpty()) {
        title = when (sharedViewModel.idBrand.toInt()) {
            Brand.Guatemala.id -> R.string.credit_bank_title_gt
            else -> R.string.credit_bank_title
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
        viewModel.onUIEvent(CreditBankViewModel.UIEvent.OnValidateForm)
        sharedViewModel.onUIEvent(CreditViewModel.UIEvent.OnSetNavigation(nextAction = {
            viewModel.onUIEvent(
                CreditBankViewModel.UIEvent.OnNextActionClick(
                    user = sharedViewModel.email,
                    nextStepAction = {
                        sharedViewModel.onUIEvent(
                            CreditViewModel.UIEvent.OnCallMutationSaveCreditFlowStep
                        )
                    },
                    saveCreditStepsHelper = sharedViewModel.saveCreditStepsHelper
                )
            )
        }, nextStep = CreditStep.Three.id, previousStep = CreditStep.One.id))
        viewModel.onUIEvent(
            CreditBankViewModel.UIEvent.OnCallQueryBanksAndRegularExpression(
                sharedViewModel.pkUser.toInt(),
                sharedViewModel.email,
                sharedViewModel.idBrand.toInt(),
                sharedViewModel.idUserRequest,
                sharedViewModel.screenConfig,
                onLoadingValueChange = { isLoading ->
                    sharedViewModel.onUIEvent(CreditViewModel.UIEvent.OnLoadingValueChange(isLoading))
                },
                onFailureWithDialog = { isLoading, dialogParameter ->
                    sharedViewModel.onUIEvent(CreditViewModel.UIEvent.OnFailureWithDialog(isLoading, dialogParameter))
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
            style = Typography.h5.copy(
                color = MultimoneyTheme.colors.text,
                fontWeight = FontWeight.SemiBold
            )
        )

        Row(modifier = Modifier.padding(top = 24.dp)) {
            CustomImage(
                drawableResource = R.drawable.ic_information,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Text(
                text = stringResource(id = R.string.credit_bank_condition),
                style = Typography.body2.copy(color = MultimoneyTheme.colors.labelText),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 9.dp)
                    .align(Alignment.CenterVertically),
            )
        }

        CustomDropdown(
            modifier = Modifier
                .wrapContentSize(Alignment.TopStart)
                .focusable(false)
                .padding(top = 16.dp),
            items = viewModel.uiState.backList,
            onValueChange = {
                viewModel.onUIEvent(CreditBankViewModel.UIEvent.OnBankValueChanged(it))
            },
            labelText = stringResource(id = R.string.credit_bank_account_destiny),
            value = viewModel.uiState.bankSelected,
            placeHolder = stringResource(id = R.string.credit_bank_select)
        )

        CustomDropdown(
            modifier = Modifier
                .wrapContentSize(Alignment.TopStart)
                .focusable(false)
                .padding(top = 16.dp),
            items = viewModel.uiState.accountTypeListFiltered?.map { it?.description ?: "" } ?: listOf(),
            onValueChange = { value ->
                viewModel.onUIEvent(CreditBankViewModel.UIEvent.OnAccountTypeValueChanged(
                    viewModel.uiState.accountTypeListFiltered?.findLast { it?.description == value }
                ))
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
                keyboardType = KeyboardType.Number, imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            }),
            labelText = stringResource(id = R.string.credit_bank_account_number_label),
            isRequiredMessage = stringResource(id = R.string.credit_bank_account_number_required),
            onValueChange = {
                viewModel.onUIEvent(
                    CreditBankViewModel.UIEvent.OnAccountNumberValueChange(
                        it
                    )
                )
            },
            isError = viewModel.uiState.accountNumberError.first,
            errorMessage = stringResource(id = viewModel.uiState.accountNumberError.second),
        )
    }
}