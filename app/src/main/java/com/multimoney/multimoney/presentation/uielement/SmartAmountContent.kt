package com.multimoney.multimoney.presentation.uielement

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.GrayScale800
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.credit.origination.amount.CreditAmountViewModel
import com.multimoney.multimoney.presentation.util.addTextStyleToTextPortion
import com.multimoney.multimoney.presentation.util.filterInvalidAmountInput
import com.multimoney.multimoney.presentation.util.transformation.CurrencyDoubleTransformation

@Composable
fun SmartAmountContent(
    @StringRes titleId: Int,
    originAccountSubtitle: String?,
    currentAmount: String,
    amountErrorMessage: String? = null,
    isAmountError: Boolean? = null,
    @StringRes amountPlaceHolderId: Int,
    suggestions: @Composable () -> Unit = {},
    onAmountChange: (String) -> Unit,
    onDebounceValidation: (String) -> Unit,
    currency: String,
    shouldDisplayExchange: Boolean,
    exchangeRate: String = "",
    convertedTotal: String = "",
    onContinueClick: () -> Unit,
    enableButton: Boolean,
    motive: String? = null,
    onMotiveChange: (String) -> Unit
) {
    val background: Color

    if (isSystemInDarkTheme()) {
        background = GrayScale800
    } else {
        background = GrayScale800
    }

    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(background)
            .padding(start = 16.dp, end = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                modifier = Modifier.padding(top = 30.dp),
                text = stringResource(id = titleId),
                style = Typography.h6.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MultimoneyTheme.colors.labelText
                ),
                textAlign = TextAlign.Left
            )
            originAccountSubtitle?.let {
                Text(
                    modifier = Modifier.padding(top = 16.dp),
                    text = stringResource(
                        id = R.string.smart_iban_transfer_from_account,
                        originAccountSubtitle
                    ).addTextStyleToTextPortion(
                        originAccountSubtitle,
                        Typography.body2.copy(fontWeight = FontWeight.SemiBold)
                    )
                )
            }
            CurrencyAmountInput(
                modifier = Modifier.padding(top = 24.dp),
                value = currentAmount,
                placeHolder = stringResource(id = amountPlaceHolderId),
                onValueChange = {
                    onAmountChange(it.filterInvalidAmountInput())
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = if (motive != null) {
                        ImeAction.Next
                    } else {
                        ImeAction.Done
                    }
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                    },
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                ),
                isRequired = true,
                customTransformation = CurrencyDoubleTransformation(
                    currency,
                    CreditAmountViewModel.CURRENCY_SEPARATOR
                ),
                onDebounceValidation = onDebounceValidation,
                errorMessage = amountErrorMessage,
                isError = isAmountError == true
            )
            suggestions()
            if (shouldDisplayExchange) {
                Spacer(modifier = Modifier.height(24.dp))
                VoucherCurrencyExchangeInfo(
                    displayIcon = false,
                    mainRowAlignment = Arrangement.SpaceAround,
                    textColumnAlign = Alignment.CenterHorizontally,
                    leftTitleResource = R.string.payment_amount_bottom_sheet_exchange_type,
                    rightTitleResource = R.string.smart_saving_total_to_deposit,
                    exchangeRateText = exchangeRate,
                    convertedAmountText = convertedTotal
                )
            }
            motive?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    modifier = Modifier.padding(bottom = 8.dp),
                    text = stringResource(id = R.string.smart_iban_transfer_motive_label),
                    style = Typography.body2
                )
                CustomOutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = motive,
                    onValueChange = {
                        onMotiveChange(it)
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    )
                )
            }
        }
        CustomButton(
            modifier = Modifier
                .height(48.dp)
                .fillMaxWidth(),
            onClick = {
                focusManager.clearFocus()
                onContinueClick()
            },
            text = stringResource(id = R.string.button_continue),
            buttonType = CustomButtonType.PrimaryPrimary,
            enable = enableButton
        )
    }
}