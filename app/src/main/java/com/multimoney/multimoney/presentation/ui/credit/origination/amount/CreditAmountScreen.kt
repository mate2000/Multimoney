package com.multimoney.multimoney.presentation.ui.credit.origination.amount

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.CreditStep
import com.multimoney.multimoney.R
import com.multimoney.multimoney.R.drawable
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.PoppinsFontFamily
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel
import com.multimoney.multimoney.presentation.ui.credit.origination.amount.CreditAmountViewModel.BaseEvent.OnOpenConditionOfCreditDialog
import com.multimoney.multimoney.presentation.ui.credit.origination.amount.CreditAmountViewModel.Companion.CURRENCY_SEPARATOR
import com.multimoney.multimoney.presentation.ui.credit.origination.amount.CreditAmountViewModel.Companion.SLIDER_TOTAL
import com.multimoney.multimoney.presentation.ui.credit.origination.amount.CreditAmountViewModel.Companion.TERMS_AND_CONDITIONS_CURRENT_FLOW
import com.multimoney.multimoney.presentation.ui.credit.origination.amount.CreditAmountViewModel.UIEvent.OnCurrencyIndexChanged
import com.multimoney.multimoney.presentation.ui.credit.origination.amount.CreditAmountViewModel.UIEvent.OnDisbursementValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.amount.CreditAmountViewModel.UIEvent.OnTermAndConditionCheckedChange
import com.multimoney.multimoney.presentation.ui.credit.origination.amount.CreditAmountViewModel.UIEvent.OnCallMutationSaveTermsAndConditionsCreditUseCase
import com.multimoney.multimoney.presentation.ui.credit.origination.amount.skeleton.CreditAmountScreenSkeleton
import com.multimoney.multimoney.presentation.ui.credit.origination.amount.termandcondition.CreditTermsAndCondition
import com.multimoney.multimoney.presentation.uielement.CurrencyAmountInput
import com.multimoney.multimoney.presentation.uielement.CustomCheckBox
import com.multimoney.multimoney.presentation.uielement.CustomInformativeChip
import com.multimoney.multimoney.presentation.uielement.CustomSlider
import com.multimoney.multimoney.presentation.uielement.CustomToggleButton
import com.multimoney.multimoney.presentation.uielement.Size.Large
import com.multimoney.multimoney.presentation.util.NavEvent
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
            disbursementProgressFactorErrorMessage = R.string.credit_amount_disbursement_progress_factor_error_message,
            minimumDisbursementErrorMessage = R.string.credit_amount_disbursement_minimum_error_message,
            maximumDisbursementErrorMessage = R.string.credit_amount_disbursement_maximum_error_message
        )
    )

    LaunchedEffect(key1 = true) {
        viewModel.executeNavigation(onNavigate = onNavigate)
        sharedViewModel.onUIEvent(CreditViewModel.UIEvent.OnContinueEnable(true))
        sharedViewModel.onUIEvent(
            CreditViewModel.UIEvent.OnSetNavigation(nextAction = {
                viewModel.onUIEvent(
                    OnCallMutationSaveTermsAndConditionsCreditUseCase(
                        user = sharedViewModel.email,
                        idBrand = sharedViewModel.idBrand.toInt(),
                        pkUser = sharedViewModel.pkUser.toLong(),
                        currentFlow = TERMS_AND_CONDITIONS_CURRENT_FLOW,
                        identification = sharedViewModel.identification,
                        descPromotion = "",
                        idPromotion = 1,
                        onSuccess = { screenConfigData ->
                            sharedViewModel.onUIEvent(CreditViewModel.UIEvent.OnUpdateScreenConfigData(screenConfigData))
                            sharedViewModel.onUIEvent(
                                CreditViewModel.UIEvent.OnNextStep
                            )
                        },
                        onLoadingValueChange = { isLoading ->
                            sharedViewModel.onUIEvent(CreditViewModel.UIEvent.OnLoadingValueChange(isLoading))
                        },
                        onFailureWithDialog = { isLoading, dialogParameter ->
                            sharedViewModel.onUIEvent(
                                CreditViewModel.UIEvent.OnFailureWithDialog(
                                    isLoading,
                                    dialogParameter
                                )
                            )
                        },
                        onCurrencySymbolValueChange = { currencySymbol ->
                            sharedViewModel.onUIEvent(CreditViewModel.UIEvent.OnCurrencySymbolValueChange(currencySymbol))
                        }
                    )
                )
            }, nextStep = CreditStep.Two.id, previousStep = CreditStep.One.id)
        )

        viewModel.onUIEvent(
            CreditAmountViewModel.UIEvent.OnCallQueryCreditOfferUseCase(
                pkUser = sharedViewModel.pkUser.toInt(),
                idBrand = sharedViewModel.idBrand.toInt(),
                onFailureWithDialog = { isLoading, dialogParameter ->
                    sharedViewModel.onUIEvent(CreditViewModel.UIEvent.OnFailureWithDialog(isLoading, dialogParameter))
                }
            )
        )

        viewModel.baseEvent.collect { event ->
            when (event) {
                is CreditAmountViewModel.BaseEvent.OnFormValidateCompleted -> sharedViewModel.onUIEvent(
                    CreditViewModel.UIEvent.OnContinueEnable(
                        event.isFormValid
                    )
                )
                is OnOpenConditionOfCreditDialog -> sharedViewModel.onUIEvent(
                    CreditViewModel.UIEvent.OnOpenDialogValueChange(
                        event.dialogParameters
                    )
                )
            }
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.onUIEvent(CreditAmountViewModel.UIEvent.OnValidateForm)
    }

    viewModel.onUIEvent(CreditAmountViewModel.UIEvent.OnInitializeText(stringResource(id = R.string.credit_amount_condition_of_credit_modal_description)))

    var title = R.string.empty
    if (sharedViewModel.idBrand.isNotEmpty()) {
        title = when (sharedViewModel.idBrand.toInt()) {
            Brand.Guatemala.id -> R.string.credit_amount_title_gt
            else -> R.string.credit_amount_title
        }
    }

    if (viewModel.uiState.isLoading) {
        CreditAmountScreenSkeleton()
    } else {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 36.dp, bottom = 40.dp),
                text = stringResource(id = title),
                style = Typography.h5.copy(
                    color = MultimoneyTheme.colors.text,
                    fontWeight = FontWeight.SemiBold
                )
            )
            if (viewModel.uiState.isMultipleCurrency) {
                CustomToggleButton(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .wrapContentSize()
                        .align(Alignment.CenterHorizontally),
                    selectedIndex = viewModel.uiState.currencyIndex,
                    items = viewModel.uiState.currencyItems,
                    onIndexChanged = { index -> viewModel.onUIEvent(OnCurrencyIndexChanged(index)) }
                )
            }
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
                modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
                isRequired = true,
                isRequiredMessage = stringResource(id = R.string.credit_amount_disbursement_minimum_error_message),
                isError = viewModel.uiState.disbursementError.first,
                errorMessage = stringResource(
                    id = viewModel.uiState.disbursementError.second,
                    viewModel.uiState.currencyItems[viewModel.uiState.currencyIndex],
                    viewModel.uiState.progressFactor ?: 0.0
                ),
                customTransformation = CurrencyIntegerTransformation(
                    viewModel.uiState.currencyItems[viewModel.uiState.currencyIndex],
                    CURRENCY_SEPARATOR
                ),
                onDebounceValidation = {
                    viewModel.onUIEvent(
                        CreditAmountViewModel.UIEvent.OnDisbursementValueChangeFinished(
                            value = it,
                            user = sharedViewModel.email,
                            idBrand = sharedViewModel.idBrand.toInt(),
                            onLoadingValueChange = { isLoading ->
                                sharedViewModel.onUIEvent(CreditViewModel.UIEvent.OnLoadingValueChange(isLoading))
                            },
                            onFailureWithDialog = { isLoading, dialogParameter ->
                                sharedViewModel.onUIEvent(
                                    CreditViewModel.UIEvent.OnFailureWithDialog(
                                        isLoading,
                                        dialogParameter
                                    )
                                )
                            }
                        )
                    )
                }
            )

            CustomSlider(
                modifier = Modifier.padding(16.dp),
                value = viewModel.uiState.sliderValue,
                valueRangeInitial = viewModel.uiState.sliderValueRangeInitial,
                valueRangeFinal = SLIDER_TOTAL.toFloat(),
                minimumLabel = viewModel.uiState.minimumDisbursementLabel,
                maximumLabel = viewModel.uiState.maximumDisbursementLabel,
                onValueChange = {
                    viewModel.onUIEvent(CreditAmountViewModel.UIEvent.OnSliderValueChange(it))
                },
                onValueChangeFinished = {
                    viewModel.onUIEvent(
                        CreditAmountViewModel.UIEvent.OnSliderValueChangeFinished(
                            user = sharedViewModel.email,
                            idBrand = sharedViewModel.idBrand.toInt(),
                            onLoadingValueChange = { isLoading ->
                                sharedViewModel.onUIEvent(CreditViewModel.UIEvent.OnLoadingValueChange(isLoading))
                            },
                            onFailureWithDialog = { isLoading, dialogParameter ->
                                sharedViewModel.onUIEvent(
                                    CreditViewModel.UIEvent.OnFailureWithDialog(
                                        isLoading,
                                        dialogParameter
                                    )
                                )
                            }
                        )
                    )
                }
            )

            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .height(1.dp),
                color = MultimoneyTheme.colors.dividerWhite16
            )
            CustomInformativeChip(
                text = stringResource(id = R.string.credit_amount_condition_of_credit_info),
                textStyle = Typography.body2.copy(color = MultimoneyTheme.colors.labelText),
                modifier = Modifier.padding(top = 16.dp),
                onClick = { viewModel.onUIEvent(CreditAmountViewModel.UIEvent.OnOpenConditionCreditDialog) },
                shape = RoundedCornerShape(24.dp),
                background = MultimoneyTheme.colors.backgroundInformativeChip,
                startIcon = R.drawable.ic_information,
                startIconTint = MultimoneyTheme.colors.textInformation,
                size = Large
            )
            CreditInfo(
                iconId = drawable.ic_money_gray,
                textId = string.credit_amount_monthly_fee,
                value = viewModel.uiState.feeLabel
            )
            CreditInfo(
                iconId = drawable.ic_percentage,
                textId = string.credit_amount_interest,
                value = viewModel.uiState.regularInterestRateLabel
            )
            CreditInfo(
                iconId = drawable.ic_calendar,
                textId = string.credit_amount_term,
                value = stringResource(id = string.credit_amount_term_value, viewModel.uiState.termLabel ?: "")
            )
            CreditInfo(
                iconId = drawable.ic_percentage,
                textId = string.credit_amount_commission_for_disbursement,
                value = viewModel.uiState.commissionDisbursementLabel
            )
            Divider(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
                    .height(1.dp),
                color = MultimoneyTheme.colors.dividerWhite16
            )
            Row(modifier = Modifier.padding(top = 24.dp), verticalAlignment = CenterVertically) {
                CustomCheckBox(
                    checked = viewModel.uiState.isTermAndConditionChecked,
                    onCheckedChange = {
                        viewModel.onUIEvent(
                            CreditAmountViewModel.UIEvent.OnTermAndConditionCheckedChange(
                                it
                            )
                        )
                    },
                    text = stringResource(id = R.string.credit_amount_term_and_conditions_first)
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
    }

    if (viewModel.uiState.isTermAndConditionDialogActive.value) {
        CreditTermsAndCondition(
            onAcceptTermsAndCondition = {
                viewModel.onUIEvent(
                    OnTermAndConditionCheckedChange(
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
fun CreditInfo(iconId: Int, textId: Int, value: String?) {
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
            text = value ?: "",
            style = Typography.subtitle1.copy(
                fontWeight = FontWeight.SemiBold,
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false
                )
            ),
            color = MultimoneyTheme.colors.labelText
        )
    }
}
