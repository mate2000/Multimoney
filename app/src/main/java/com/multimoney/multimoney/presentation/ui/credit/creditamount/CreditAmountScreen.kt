package com.multimoney.multimoney.presentation.ui.credit.creditamount

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.CreditStep
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.PoppinsFontFamily
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.theme.WhiteTransparency10
import com.multimoney.multimoney.presentation.ui.credit.CreditViewModel
import com.multimoney.multimoney.presentation.ui.credit.creditamount.CreditAmountViewModel.BaseEvent.OnOpenConditionOfCreditDialog
import com.multimoney.multimoney.presentation.ui.credit.creditamount.termandcondition.CreditTermAndCondition
import com.multimoney.multimoney.presentation.uielement.CustomCheckBox
import com.multimoney.multimoney.presentation.uielement.CustomInformativeChip
import com.multimoney.multimoney.presentation.uielement.Size.Large
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.ui.credit.creditamount.CreditAmountViewModel.Companion.CURRENCY_SEPARATOR
import com.multimoney.multimoney.presentation.ui.credit.creditamount.CreditAmountViewModel.UIEvent.OnCurrencyIndexChanged
import com.multimoney.multimoney.presentation.ui.credit.creditamount.CreditAmountViewModel.UIEvent.OnDisbursementValueChange
import com.multimoney.multimoney.presentation.uielement.CurrencyAmountInput
import com.multimoney.multimoney.presentation.uielement.CustomToggleButton
import com.multimoney.multimoney.presentation.util.transformation.CurrencyIntegerTransformation

@Composable
@Preview
fun CreditAmountScreen(
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    sharedViewModel: CreditViewModel = hiltViewModel(),
    viewModel: CreditAmountViewModel = hiltViewModel()
) {

    // Properties
    val focusManager = LocalFocusManager.current

    viewModel.onUIEvent(
        CreditAmountViewModel.UIEvent.OnInitializeErrorMessages(
            minimumDisbursementErrorMessage = R.string.credit_amount_disbursement_minimum_error_message,
            maximumDisbursementErrorMessage = R.string.credit_amount_disbursement_maximum_error_message
        )
    )

    LaunchedEffect(key1 = true) {
        viewModel.executeNavigation(onNavigate = onNavigate)
        sharedViewModel.onUIEvent(CreditViewModel.UIEvent.OnContinueEnable(true))
        sharedViewModel.onUIEvent(CreditViewModel.UIEvent.OnSetNavigation(nextAction = {
            viewModel.onUIEvent(CreditAmountViewModel.UIEvent.OnNextActionClick {
                sharedViewModel.onUIEvent(
                    CreditViewModel.UIEvent.OnNextStep
                )
            })
        }, nextStep = CreditStep.Two.id, previousStep = CreditStep.One.id))

        viewModel.onUIEvent(
            // TODO: Send appropriate data for this call because now we don't have this data
            CreditAmountViewModel.UIEvent.OnCallQueryCreditOfferUseCase(
                onLoadingValueChange = { isLoading ->
                    sharedViewModel.onUIEvent(CreditViewModel.UIEvent.OnLoadingValueChange(isLoading))
                },
                onFailureWithDialog = { isLoading, dialogParameter ->
                    sharedViewModel.onUIEvent(CreditViewModel.UIEvent.OnFailureWithDialog(isLoading, dialogParameter))
                }
            )
        )

        viewModel.baseEvent.collect { event ->
            when (event) {
                is OnOpenConditionOfCreditDialog -> sharedViewModel.onUIEvent(
                    CreditViewModel.UIEvent.OnOpenDialogValueChange(
                        event.dialogParameters
                    )
                )
            }
        }
    }

    viewModel.onUIEvent(CreditAmountViewModel.UIEvent.OnInitializeText(stringResource(id = R.string.credit_amount_condition_of_credit_modal_description)))

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 36.dp, bottom = 40.dp),
            text = stringResource(id = R.string.credit_amount_title),
            style = Typography.h5.copy(
                color = MultimoneyTheme.colors.text,
                fontWeight = FontWeight.SemiBold
            )
        )
        CustomToggleButton(
            modifier = Modifier
                .wrapContentSize()
                .align(Alignment.CenterHorizontally),
            selectedIndex = viewModel.uiState.currencyIndex,
            items = viewModel.uiState.currencyItems,
            onIndexChanged = { index -> viewModel.onUIEvent(OnCurrencyIndexChanged(index)) }
        )
        CurrencyAmountInput(
            value = viewModel.uiState.disbursement,
            placeHolder = stringResource(
                id = R.string.credit_amount_disbursement_placeholder,
                viewModel.uiState.currencyItems[viewModel.uiState.currencyIndex]
            ),
            onValueChange = {
                viewModel.onUIEvent(OnDisbursementValueChange(it))
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            }),
            modifier = Modifier.padding(top = 16.dp),
            isRequired = true,
            isRequiredMessage = stringResource(id = R.string.credit_amount_disbursement_minimum_error_message),
            isError = viewModel.uiState.disbursementError.first,
            errorMessage = stringResource(id = viewModel.uiState.disbursementError.second),
            customTransformation = CurrencyIntegerTransformation(
                viewModel.uiState.currencyItems[viewModel.uiState.currencyIndex],
                CURRENCY_SEPARATOR
            ),
            onDebounceValidation = {
                viewModel.onUIEvent(
                    CreditAmountViewModel.UIEvent.OnValidateDisbursement(it)
                )
            }
        )
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp),
            color = MultimoneyTheme.colors.divider
        )
        CustomInformativeChip(
            text = stringResource(id = R.string.credit_amount_condition_of_credit_info),
            textStyle = Typography.body2.copy(color = MultimoneyTheme.colors.labelText),
            modifier = Modifier.padding(top = 16.dp),
            onClick = { viewModel.onUIEvent(CreditAmountViewModel.UIEvent.OnOpenConditionCreditDialog) },
            shape = RoundedCornerShape(24.dp),
            background = WhiteTransparency10,
            startIcon = R.drawable.ic_information,
            startIconTint = MultimoneyTheme.colors.textInformation,
            size = Large
        )
        CreditInfo(iconId = R.drawable.ic_money_gray, textId = R.string.credit_amount_monthly_fee, value = "1000")
        CreditInfo(
            iconId = R.drawable.ic_percentage,
            textId = R.string.credit_amount_interest,
            value = viewModel.uiState.interest
        )
        CreditInfo(
            iconId = R.drawable.ic_calendar,
            textId = R.string.credit_amount_term,
            value = stringResource(id = R.string.credit_amount_term_value, viewModel.uiState.term)
        )
        CreditInfo(
            iconId = R.drawable.ic_percentage,
            textId = R.string.credit_amount_commission_for_disbursement,
            value = viewModel.uiState.commission
        )
        Divider(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
                .height(1.dp),
            color = MultimoneyTheme.colors.divider
        )
        Row(modifier = Modifier.padding(top = 24.dp), verticalAlignment = CenterVertically) {
            CustomCheckBox(
                checked = viewModel.uiState.isTermAndConditionChecked,
                onCheckedChange = { viewModel.onUIEvent(CreditAmountViewModel.UIEvent.OnTermAndConditionCheckedChange(it)) },
                text = stringResource(id = R.string.credit_amount_term_and_conditions_first),
            )
            ClickableText(
                text = AnnotatedString(stringResource(id = R.string.credit_amount_term_and_conditions_second)),
                style = TextStyle(
                    fontFamily = PoppinsFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    color = MultimoneyTheme.colors.textLink,
                    fontSize = 15.sp,
                    textDecoration = TextDecoration.Underline
                ),
                modifier = Modifier
                    .wrapContentSize()
                    .padding(start = 4.dp),
                onClick = {
                    viewModel.onUIEvent(CreditAmountViewModel.UIEvent.OnOpenTermAndCondition)
                }
            )
        }
    }

    if (viewModel.uiState.isTermAndConditionDialogActive.value) {
        CreditTermAndCondition(
            onAcceptTermsAndCondition = {
                viewModel.onUIEvent(
                    CreditAmountViewModel.UIEvent.OnTermAndConditionCheckedChange(
                        true
                    )
                )
            },
            isActive = viewModel.uiState.isTermAndConditionDialogActive
        )
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun CreditInfo(iconId: Int, textId: Int, value: String) {
    Row(modifier = Modifier.padding(top = 12.dp), verticalAlignment = CenterVertically) {
        Icon(
            painter = painterResource(id = iconId),
            contentDescription = "",
            modifier = Modifier.size(16.dp, 16.dp),
            tint = MultimoneyTheme.colors.iconColor
        )
        Text(
            text = stringResource(id = textId),
            modifier = Modifier.padding(start = 9.dp),
            style = Typography.subtitle1.copy(
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false
                )
            ),
            color = MultimoneyTheme.colors.labelText
        )
        Text(
            text = value,
            style = Typography.subtitle1.copy(
                fontWeight = FontWeight.SemiBold, platformStyle = PlatformTextStyle(
                    includeFontPadding = false
                )
            ),
            color = MultimoneyTheme.colors.labelText
        )
    }
}