package com.multimoney.multimoney.presentation.ui.smart.payment

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
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnProfessionValueChange
import com.multimoney.multimoney.presentation.ui.smart.payment.SmartCrSalaryViewModel.UIEvent.OnCallQueryProfessionUseCase
import com.multimoney.multimoney.presentation.ui.smart.payment.SmartCrSalaryViewModel.UIEvent.OnPaymentAmountChange
import com.multimoney.multimoney.presentation.ui.smart.payment.SmartCrSalaryViewModel.UIEvent.OnProfessionChange
import com.multimoney.multimoney.presentation.ui.smart.payment.SmartCrSalaryViewModel.UIEvent.OnValidateForm
import com.multimoney.multimoney.presentation.uielement.CustomDropdown
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.util.transformation.formatMoney

@Composable
fun SmartCrSalaryScreen(viewModel: SmartCrSalaryViewModel = hiltViewModel(), sharedViewModel: SmartViewModel) {
    val focusManager = LocalFocusManager.current

    LaunchedEffect(true) {
        viewModel.onUIEvent(OnCallQueryProfessionUseCase(sharedViewModel.uiState.user,
            sharedViewModel.uiState.idBrand.toInt()))

        viewModel.onUIEvent(OnValidateForm)
    }

    Column(modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp)) {
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
            customTransformation = formatMoney("₡")
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
                viewModel.onUIEvent(OnProfessionChange(it) { professionId ->
                    sharedViewModel.onUIEvent(OnProfessionValueChange(it,
                        professionId))
                })
            },
            labelText = stringResource(id = string.smart_account_formal_select_profession),
            placeHolder = stringResource(id = string.select)
        )
    }
}