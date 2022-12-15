package com.multimoney.multimoney.presentation.ui.credit.payment.cards.amount

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.credit.payment.cards.amount.PaymentAmountCardViewModel.UIEvent
import com.multimoney.multimoney.presentation.ui.credit.payment.cards.amount.PaymentAmountCardViewModel.UIEvent.OnAlertResultButtonClick
import com.multimoney.multimoney.presentation.ui.credit.payment.cards.amount.PaymentAmountCardViewModel.UIEvent.OnHidePaymentBottomSheet
import com.multimoney.multimoney.presentation.ui.credit.payment.cards.amount.PaymentAmountCardViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.credit.payment.cards.amount.PaymentAmountCardViewModel.UIEvent.OnStart
import com.multimoney.multimoney.presentation.uielement.AlertResult
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.uielement.VisaAnimation
import com.multimoney.multimoney.presentation.util.NavEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun PaymentAmountCardScreen(
    isRestart: Boolean = true,
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    viewModel: PaymentAmountCardViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(true) {
        viewModel.apply {
            executeNavigation(onPopBackStack = onPopBackStack, onNavigate = onNavigate)
            onUIEvent(OnStart)
        }
    }
    PaymentAmountCardContent(viewModel, coroutineScope)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
@Preview
fun PaymentAmountCardContent(
    viewModel: PaymentAmountCardViewModel = hiltViewModel(),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) {
    val focusManager = LocalFocusManager.current
    if (viewModel.uiState.isAlertResultVisible) {
        AlertResult(
            titleString = viewModel.uiState.alertResultTitle,
            descriptionString = viewModel.uiState.alertResultDescription,
            buttonTextResource = string.payment_amount_error_button,
            isLeftButtonVisible = false,
            isRightButtonVisible = false,
            onButtonClick = { viewModel.onUIEvent(OnAlertResultButtonClick) }
        )
    } else {
        Column(
            modifier = Modifier.background(MultimoneyTheme.colors.background)
        ) {
            TopNavBar(
                onLeftButtonClick = { viewModel.onUIEvent(UIEvent.OnNavigateBack) },
                onRightButtonClick = { viewModel.onUIEvent(UIEvent.OnNavigateBackHome) }
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .background(MultimoneyTheme.colors.background)
                    .padding(start = 16.dp, end = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        modifier = Modifier.padding(top = 42.dp),
                        text = stringResource(id = R.string.payment_amount_card_title),
                        style = Typography.h5.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MultimoneyTheme.colors.labelText
                        ),
                        textAlign = TextAlign.Left
                    )
                }
                CustomButton(
                    modifier = Modifier
                        .height(48.dp)
                        .fillMaxWidth(),
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.onUIEvent(UIEvent.OnContinueClick)
                    },
                    text = stringResource(id = R.string.button_continue),
                    buttonType = CustomButtonType.PrimaryPrimary,
                    enable = viewModel.uiState.enableButton
                )
            }
        }
    }

    if (viewModel.uiState.isVisaAnimationVisible) {
        VisaAnimation { viewModel.onUIEvent(UIEvent.OnFinishVisaAnimation) }
    }
    PaymentAmountCardBottomSheetScreen(
        viewModel,
        coroutineScope,
        viewModel.uiState.bottomSheetVisibleState
    )
    BackHandler {
        when {
            viewModel.uiState.bottomSheetVisibleState.isVisible -> {
                coroutineScope.launch {
                    viewModel.onUIEvent(OnHidePaymentBottomSheet)
                }
            }
            else -> viewModel.onUIEvent(OnNavigateBack)
        }
    }
    LoadingIndicator(viewModel.uiState.isLoading)
}
