package com.multimoney.multimoney.presentation.ui.credit.origination.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.CreditStep
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.credit.origination.account.CrosselingAccountViewModel.UIEvent.OnClientBankAccountSelected
import com.multimoney.multimoney.presentation.ui.credit.origination.account.CrosselingAccountViewModel.UIEvent.OnNextActionClick
import com.multimoney.multimoney.presentation.ui.credit.origination.account.CrosselingAccountViewModel.UIEvent.OnStart
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.CustomInfoButton
import com.multimoney.multimoney.presentation.util.catalog.AdjustEventType
import com.multimoney.multimoney.presentation.util.getCurrencyFromId
import com.multimoney.multimoney.presentation.util.getMaskedAccount

@Composable
fun CrosselingAccountScreen(
    sharedViewModel: CreditViewModel, viewModel: CrosselingAccountViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {
        sharedViewModel.onUIEvent(
            CreditViewModel.UIEvent.OnSetNavigation(
                nextAction = {
                    viewModel.onUIEvent(
                        OnNextActionClick(
                            user = sharedViewModel.email, nextStepAction = {
                                sharedViewModel.onUIEvent(CreditViewModel.UIEvent.OnCallMutationSaveCreditFlowStep)
                            }, saveCreditStepsHelper = sharedViewModel.saveCreditStepsHelper
                        )
                    )
                    sharedViewModel.logEvents(
                        AdjustEventType.CROSSELLING_FIRST_FILL_ACCOUNT_5028
                    )
                }, nextStep = if (sharedViewModel.idBrand.toInt() == Brand.ElSalvador.id) {
                    CreditStep.Three.id
                } else {
                    CreditStep.Four.id
                }, previousStep = CreditStep.One.id
            )
        )
        viewModel.onUIEvent(OnStart(pkUser = sharedViewModel.pkUser.toInt(),
            user = sharedViewModel.email,
            idBrand = sharedViewModel.idBrand.toInt(),
            idUserRequest = sharedViewModel.idUserRequest,
            identification = sharedViewModel.identification,
            country = "",
            idAccount = 0,
            accountNumber = "",
            onLoadingValueChange = { isLoading ->
                sharedViewModel.onUIEvent(CreditViewModel.UIEvent.OnLoadingValueChange(isLoading))
            },
            onFailureWithDialog = { isLoading, dialogParameter ->
                sharedViewModel.onUIEvent(
                    CreditViewModel.UIEvent.OnFailureWithDialog(
                        isLoading, dialogParameter
                    )
                )
            }) { isEmpty ->
            if (isEmpty) {
                sharedViewModel.onUIEvent(CreditViewModel.UIEvent.NavigateToAccountScreen)
            }
            sharedViewModel.onUIEvent(CreditViewModel.UIEvent.OnSetBankListEmpty(isEmpty))
        })
    }

    PaymentAccountContent(viewModel, sharedViewModel)
}

@Composable
@Preview
fun PaymentAccountContent(
    viewModel: CrosselingAccountViewModel = hiltViewModel(), sharedViewModel: CreditViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier
            .background(MultimoneyTheme.colors.background)
            .fillMaxSize()
    ) {
        Text(
            modifier = Modifier.padding(top = 8.dp, start = 24.dp, end = 24.dp, bottom = 20.dp),
            text = stringResource(id = viewModel.uiState.titleResource),
            style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.labelText,
            textAlign = TextAlign.Left
        )

        PaymentAccountList(viewModel, sharedViewModel)

        if (viewModel.uiState.openDialog.isActive.value) {
            CustomDialog(
                title = stringResource(id = viewModel.uiState.openDialog.titleResource),
                message = stringResource(id = viewModel.uiState.openDialog.descriptionResource).ifEmpty { viewModel.uiState.openDialog.description },
                positiveButtonText = stringResource(id = viewModel.uiState.openDialog.positiveResource),
                openDialogCustom = viewModel.uiState.openDialog.isActive,
                onPositiveAction = viewModel.uiState.openDialog.positiveAction
            )
        }
    }
}

@Composable
@Preview
fun PaymentAccountList(
    viewModel: CrosselingAccountViewModel = hiltViewModel(), sharedViewModel: CreditViewModel = hiltViewModel()
) {
    viewModel.uiState.clientBankAccountList?.let { clientBankAccountList ->
        LazyColumn(modifier = Modifier.padding(top = 20.dp, start = 24.dp, end = 24.dp)) {
            items(clientBankAccountList) { clientBankAccount ->
                CustomInfoButton(modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                    startIcon = clientBankAccount?.currencyId?.getCurrencyFromId()?.accountIcon ?: 0,
                    title = clientBankAccount?.bank ?: "",
                    subtitle = getMaskedAccount(
                        clientBankAccount?.sinpeAccount ?: "", stringResource(id = R.string.payment_account_masked_text)
                    ),
                    endIcon = R.drawable.ic_right_chevron,
                    onClick = {
                        viewModel.onUIEvent(
                            OnClientBankAccountSelected(
                                clientBankAccount,
                                onContinueClick = {
                                    sharedViewModel.onUIEvent(OnContinueClick())
                                }
                            )
                        )
                    })
            }
        }
    }
    CustomButton(
        text = stringResource(id = R.string.payment_account_create),
        modifier = Modifier
            .padding(top = 32.dp)
            .fillMaxWidth(),
        onClick = {
            sharedViewModel.onUIEvent(CreditViewModel.UIEvent.NavigateToAccountScreen)
        },
        buttonType = CustomButtonType.PrimaryTertiary,
        trailingIcon = R.drawable.ic_plus
    )
}
