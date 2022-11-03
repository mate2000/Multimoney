package com.multimoney.multimoney.presentation.ui.smart.payment

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
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
import com.multimoney.data.util.catalog.SmartSteps.Four
import com.multimoney.data.util.catalog.SmartSteps.Two
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnCallMutationUpdateGlobalRequestUseCase
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnContinueEnable
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnContinueVisible
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnSetNavigation
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.SourceIncomeViewModel
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.SourceIncomeViewModel.UIEvent.OnNavigateToSelectedSourceOfIncomeOption
import com.multimoney.multimoney.presentation.ui.smart.payment.SmartCrSalaryViewModel.BaseEvent.OnFormValidateCompleted
import com.multimoney.multimoney.presentation.ui.smart.payment.SmartCrSalaryViewModel.UIEvent.OnCallQueryProfessionUseCase
import com.multimoney.multimoney.presentation.ui.smart.payment.SmartCrSalaryViewModel.UIEvent.OnPaymentAmountChange
import com.multimoney.multimoney.presentation.ui.smart.payment.SmartCrSalaryViewModel.UIEvent.OnProfessionChange
import com.multimoney.multimoney.presentation.ui.smart.payment.SmartCrSalaryViewModel.UIEvent.OnValidateForm
import com.multimoney.multimoney.presentation.ui.smart.payment.SmartCrSalaryViewModel.UIState
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.CustomDropdown
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.util.catalog.SourceIncomeOptionType
import com.multimoney.multimoney.presentation.util.catalog.SourceIncomeOptionType.MainSourceIncomeScreenType
import com.multimoney.multimoney.presentation.util.getCurrencySymbol
import com.multimoney.multimoney.presentation.util.transformation.formatMoney

@Composable
fun SmartCrSalaryScreen(
    viewModel: SmartCrSalaryViewModel = hiltViewModel(),
    sharedViewModel: SmartViewModel,
    sourceIncomeSharedViewModel: SourceIncomeViewModel,
) {
    val focusManager = LocalFocusManager.current

    LaunchedEffect(true) {
        sharedViewModel.onUIEvent(OnContinueVisible(true))
        viewModel.onUIEvent(
            OnCallQueryProfessionUseCase(
                sharedViewModel.user,
                sharedViewModel.idBrand.toInt()
            )
        )
        sharedViewModel.onUIEvent(OnContinueEnable(viewModel.onValidateForm()))
        viewModel.onUIEvent(OnValidateForm)

        sharedViewModel.onUIEvent(
            OnSetNavigation(
                nextAction = {
                    sharedViewModel.onUIEvent(
                        OnCallMutationUpdateGlobalRequestUseCase(
                            accountSmartData = sharedViewModel.accountSmartData?.copy(
                                idEconomicActivity = SourceIncomeOptionType.FormalSalaried.id.toLong(),
                                idProfessionType = viewModel.uiState.professionList.find { it?.name == viewModel.uiState.profession }?.id,
                                income = viewModel.uiState.paymentAmount.toInt().toFloat()
                            )
                        )
                    )
                },
                nextStep = Four.id,
                previousStep = Two.id
            )
        )

        viewModel.baseEvent.collect { event ->
            when (event) {
                is OnFormValidateCompleted -> sharedViewModel.onUIEvent(
                    OnContinueEnable(event.isFormValid)
                )
            }
        }
    }

    BackHandler {
        sourceIncomeSharedViewModel.onUIEvent(
            (OnNavigateToSelectedSourceOfIncomeOption(
                MainSourceIncomeScreenType.id
            ))
        )
    }

    ShowCustomDialog(viewModel.uiState)

    Column(
        modifier = Modifier
            .padding(vertical = 16.dp, horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
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
                    append(stringResource(id = string.smart_account_formal_title))
                }
            },
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )

        CustomOutlinedTextField(
            value = viewModel.uiState.paymentAmount,
            onValueChange = {
                viewModel.onUIEvent(OnPaymentAmountChange(it))
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            }),
            labelText = stringResource(id = string.smart_account_formal_monthly),
            modifier = Modifier
                .padding(top = 44.dp),
            placeHolder = stringResource(id = string.smart_account_formal_placeholder),
            customTransformation = formatMoney(
                stringResource(
                    id = sharedViewModel.idBrand.toInt()
                        .getCurrencySymbol()
                )
            )
        )

        CustomDropdown(
            modifier = Modifier
                .padding(top = 16.dp)
                .wrapContentSize(Alignment.TopStart)
                .focusable(false),
            items = viewModel.uiState.professionList.map { professionStatus ->
                professionStatus?.name ?: ""
            },
            value = viewModel.uiState.profession,
            onValueChange = {
                viewModel.onUIEvent(OnProfessionChange(it))
            },
            labelText = stringResource(id = string.smart_account_formal_select_profession),
            placeHolder = stringResource(id = string.select)
        )
    }
}

@Composable
fun ShowCustomDialog(uiState: UIState) {
    if (uiState.openDialog.isActive.value) {
        CustomDialog(
            title = stringResource(uiState.openDialog.titleResource),
            message = stringResource(uiState.openDialog.descriptionResource).ifEmpty { uiState.openDialog.description },
            positiveButtonText = stringResource(uiState.openDialog.positiveResource),
            openDialogCustom = uiState.openDialog.isActive,
            onPositiveAction = uiState.openDialog.positiveAction
        )
    }
}