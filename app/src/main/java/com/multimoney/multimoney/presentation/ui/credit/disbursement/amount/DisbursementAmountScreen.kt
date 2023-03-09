package com.multimoney.multimoney.presentation.ui.credit.disbursement.amount

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.R.drawable
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.credit.disbursement.amount.DisbursementAmountViewModel.Companion.SLIDER_ANIMATION_TIME
import com.multimoney.multimoney.presentation.ui.credit.disbursement.amount.DisbursementAmountViewModel.Companion.SLIDER_INITIAL_VALUE
import com.multimoney.multimoney.presentation.ui.credit.disbursement.amount.DisbursementAmountViewModel.Companion.SLIDER_TOTAL
import com.multimoney.multimoney.presentation.ui.credit.disbursement.amount.DisbursementAmountViewModel.Companion.SLIDER_TOTAL_ANIMATION_VALUE
import com.multimoney.multimoney.presentation.ui.credit.disbursement.amount.DisbursementAmountViewModel.UIEvent.OnAnimationFinish
import com.multimoney.multimoney.presentation.ui.credit.disbursement.amount.DisbursementAmountViewModel.UIEvent.OnCloseClick
import com.multimoney.multimoney.presentation.ui.credit.disbursement.amount.DisbursementAmountViewModel.UIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.credit.disbursement.amount.DisbursementAmountViewModel.UIEvent.OnCurrencyIndexChanged
import com.multimoney.multimoney.presentation.ui.credit.disbursement.amount.DisbursementAmountViewModel.UIEvent.OnDisbursementValueChange
import com.multimoney.multimoney.presentation.ui.credit.disbursement.amount.DisbursementAmountViewModel.UIEvent.OnDisbursementValueChangeFinished
import com.multimoney.multimoney.presentation.ui.credit.disbursement.amount.DisbursementAmountViewModel.UIEvent.OnOpenConditionCreditDialog
import com.multimoney.multimoney.presentation.ui.credit.disbursement.amount.DisbursementAmountViewModel.UIEvent.OnSliderValueChange
import com.multimoney.multimoney.presentation.ui.credit.disbursement.amount.DisbursementAmountViewModel.UIEvent.OnSliderValueChangeFinished
import com.multimoney.multimoney.presentation.ui.credit.disbursement.amount.DisbursementAmountViewModel.UIEvent.OnStart
import com.multimoney.multimoney.presentation.ui.credit.disbursement.amount.skeleton.DisbursementAmountScreenSkeleton
import com.multimoney.multimoney.presentation.uielement.CurrencyAmountInput
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType.PrimaryPrimary
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.CustomInformativeChip
import com.multimoney.multimoney.presentation.uielement.CustomSlider
import com.multimoney.multimoney.presentation.uielement.CustomToggleButton
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.Size.Large
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.transformation.formatMoney

@Composable
fun DisbursementAmountScreen(
    isRestart: Boolean,
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: DisbursementAmountViewModel = hiltViewModel()
) {
    // Properties
    val focusManager = LocalFocusManager.current

    val sliderAnimator by animateFloatAsState(
        targetValue = if (viewModel.uiState.startAnimation) SLIDER_TOTAL_ANIMATION_VALUE else SLIDER_INITIAL_VALUE,
        animationSpec = tween(durationMillis = SLIDER_ANIMATION_TIME),
        finishedListener = { progress ->
            viewModel.onUIEvent(OnAnimationFinish)
        }
    )

    viewModel.apply {
        isOnRestart = isRestart
        LaunchedEffect(isOnRestart) {
            if (isOnRestart) {
                viewModel.executeNavigation(onNavigate = onNavigate, onPopBackStack = onPopBackStack)
                viewModel.onUIEvent(OnStart)
                isOnRestart = false
            }
        }
    }

    if (viewModel.uiState.isSkeletonLoading) {
        DisbursementAmountScreenSkeleton()
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MultimoneyTheme.colors.background)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                TopNavBar(
                    onRightButtonClick = { viewModel.onUIEvent(OnCloseClick) },
                    isLeftButtonVisible = false
                )
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 35.dp, bottom = 24.dp),
                    text = stringResource(id = viewModel.uiState.titleResource),
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
                    modifier = if (viewModel.uiState.isMultipleCurrency) {
                        Modifier.padding(top = 30.dp, start = 16.dp, end = 16.dp)
                    } else {
                        Modifier.padding(top = 0.dp, start = 16.dp, end = 16.dp)
                    },
                    isRequired = true,
                    isRequiredMessage = stringResource(id = R.string.credit_amount_disbursement_minimum_error_message),
                    isError = viewModel.uiState.disbursementError.first,
                    errorMessage = stringResource(
                        id = viewModel.uiState.disbursementError.second,
                        viewModel.uiState.currencyItems[viewModel.uiState.currencyIndex],
                        viewModel.uiState.progressFactor.toInt()
                    ),
                    customTransformation = formatMoney(viewModel.uiState.currencyItems[viewModel.uiState.currencyIndex]),
                    onDebounceValidation = {
                        viewModel.onUIEvent(OnDisbursementValueChangeFinished(it))
                    }
                )

                CustomSlider(
                    modifier = Modifier.padding(16.dp),
                    value = if (viewModel.isAnimationRunning) sliderAnimator else viewModel.uiState.sliderValue,
                    valueRangeInitial = viewModel.uiState.sliderValueRangeInitial,
                    valueRangeFinal = SLIDER_TOTAL.toFloat(),
                    minimumLabel = viewModel.uiState.minimumDisbursementLabel,
                    maximumLabel = viewModel.uiState.maximumDisbursementLabel,
                    onValueChange = {
                        viewModel.onUIEvent(OnSliderValueChange(it))
                    },
                    onValueChangeFinished = {
                        viewModel.onUIEvent(OnSliderValueChangeFinished)
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
                    text = stringResource(id = R.string.disbursement_amount_info),
                    textStyle = Typography.body2.copy(color = MultimoneyTheme.colors.labelText),
                    modifier = Modifier.padding(top = 16.dp),
                    onClick = { viewModel.onUIEvent(OnOpenConditionCreditDialog) },
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
                    value = stringResource(id = string.credit_amount_term_value, viewModel.uiState.termLabel)
                )
                CreditInfo(
                    iconId = drawable.ic_percentage,
                    textId = string.credit_amount_commission_for_disbursement,
                    value = viewModel.uiState.commissionDisbursementLabel
                )
            }
            CustomButton(
                onClick = { viewModel.onUIEvent(OnContinueClick(focusManager)) },
                text = stringResource(id = string.button_continue),
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, bottom = 32.dp, top = 16.dp)
                    .fillMaxWidth()
                    .height(48.dp),
                buttonType = PrimaryPrimary,
                enable = viewModel.uiState.isFormValid
            )
        }
    }

    LoadingIndicator(viewModel.uiState.isContinue)

    if (viewModel.uiState.openDialog.isActive.value) {
        CustomDialog(
            title = stringResource(id = viewModel.uiState.openDialog.titleResource),
            message = stringResource(id = viewModel.uiState.openDialog.descriptionResource).ifEmpty { viewModel.uiState.openDialog.description },
            positiveButtonText = stringResource(id = viewModel.uiState.openDialog.positiveResource),
            negativeButtonText = stringResource(id = viewModel.uiState.openDialog.negativeResource),
            openDialogCustom = viewModel.uiState.openDialog.isActive,
            onPositiveAction = viewModel.uiState.openDialog.positiveAction,
            onNegativeAction = viewModel.uiState.openDialog.negativeAction
        )
    }

    BackHandler {
        viewModel.onUIEvent(OnCloseClick)
    }
}

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
                fontWeight = FontWeight.SemiBold,
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false
                )
            ),
            color = MultimoneyTheme.colors.labelText
        )
    }
}
