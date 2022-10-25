package com.multimoney.multimoney.presentation.ui.payment.points

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.ui.payment.points.PaymentPointsViewModel.UIEvent
import com.multimoney.multimoney.presentation.ui.payment.points.PaymentPointsViewModel.UIEvent.OnSaveArguments
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PaymentAmountScreen(
    navBackStackEntry: NavBackStackEntry,
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    viewModel: PaymentPointsViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val paymentAmountBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)

    LaunchedEffect(true) {
        viewModel.apply {
            executeNavigation(onPopAndNavigate = onPopAndNavigate)
            navBackStackEntry.arguments?.apply {
                viewModel.onUIEvent(
                    OnSaveArguments(
                        getInt(ID_BRAND)
                    )
                )
            }
        }
    }
    BackHandler {
        when {
            paymentAmountBottomSheetState.isVisible -> {
                coroutineScope.launch {
                    paymentAmountBottomSheetState.hide()
                }
            }
        }
    }
    PaymentAmountContent(viewModel)
}

@Composable
@Preview
fun PaymentAmountContent(
    viewModel: PaymentPointsViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    Column(
        modifier = Modifier
            .background(MultimoneyTheme.colors.background)
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        TopNavBar(
            onLeftButtonClick = { viewModel.onUIEvent(UIEvent.OnNavigateBack) },
            onRightButtonClick = { viewModel.onUIEvent(UIEvent.OnNavigateBack) }
        )
    }
}
