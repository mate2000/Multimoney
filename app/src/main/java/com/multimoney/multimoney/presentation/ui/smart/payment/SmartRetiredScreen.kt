package com.multimoney.multimoney.presentation.ui.smart.payment

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
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
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnContinueEnable
import com.multimoney.multimoney.presentation.ui.smart.payment.SmartRetiredViewModel.BaseEvent.OnFormValidateCompleted
import com.multimoney.multimoney.presentation.ui.smart.payment.SmartRetiredViewModel.UIEvent.OnInstitutionValueChange
import com.multimoney.multimoney.presentation.ui.smart.payment.SmartRetiredViewModel.UIEvent.OnPaymentAmountValueChange
import com.multimoney.multimoney.presentation.ui.smart.payment.SmartRetiredViewModel.UIEvent.OnValidateForm
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.util.transformation.formatMoney

@Composable
fun SmartRetiredScreen(
    viewModel: SmartRetiredViewModel = hiltViewModel(),
    sharedViewModel: SmartViewModel = hiltViewModel(),
) {
    val focusManager = LocalFocusManager.current

    LaunchedEffect(key1 = true) {
        viewModel.baseEvent.collect { event ->
            when (event) {
                is OnFormValidateCompleted -> sharedViewModel.onUIEvent(
                    OnContinueEnable(event.isFormValid)
                )
            }
        }
    }

    LaunchedEffect(true) {
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
                    append(stringResource(id = R.string.smart_account_retired_title))
                }
            },
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )

        CustomOutlinedTextField(
            value = viewModel.uiState.institution,
            onValueChange = {
                viewModel.onUIEvent(OnInstitutionValueChange(it))
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            }),
            labelText = stringResource(id = R.string.smart_account_retired_institution_label),
            modifier = Modifier
                .padding(top = 44.dp),
            placeHolder = stringResource(id = R.string.smart_account_retired_institution_placeholder)
        )

        CustomOutlinedTextField(
            value = viewModel.uiState.paymentAmount,
            onValueChange = {
                viewModel.onUIEvent(OnPaymentAmountValueChange(it))
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            }),
            labelText = stringResource(id = R.string.smart_account_retired_amount_label),
            modifier = Modifier
                .padding(top = 44.dp),
            placeHolder = stringResource(id = R.string.smart_account_retired_amount_placeholder),
            customTransformation = formatMoney("$")
        )
    }
}