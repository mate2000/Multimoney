package com.multimoney.multimoney.presentation.ui.crypto.send

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.CryptoSendSteps
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.crypto.send.listofcurrencies.CryptoSendListOfCurrenciesScreen
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun CryptoSendFlow(
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: CryptoSendSharedViewModel = hiltViewModel()
) {

    LaunchedEffect(true) {
        viewModel.onUIEvent(CryptoSendSharedViewModel.UIEvent.OnGetUserInfo)
        viewModel.executeNavigation(
            onNavigate = onNavigate,
            onPopAndNavigate = onPopAndNavigate,
            onPopBackStack = onPopBackStack
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.background)
    ) {
        Column {
            TopNavBar(
                isLeftButtonVisible = true,
                isRightButtonVisible = true,
                onRightButtonClick = {
                    viewModel.onUIEvent(CryptoSendSharedViewModel.UIEvent.OnCloseClick)
                },
                onLeftButtonClick = {
                    viewModel.onUIEvent(CryptoSendSharedViewModel.UIEvent.OnPreviousStep)
                },
            )
        }
        Column(
            modifier = Modifier.weight(0.1f),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            when (viewModel.idBrand) {
                Brand.ElSalvador.id -> {
                    /* Sending crypto is temporarily disabled to El Salvador. */
                }
                Brand.CostaRica.id -> {
                    if (viewModel.comingFromCurrencyDetails) {
                        CRSendCryptoDirectFlow(
                            step = viewModel.uiState.currentStep,
                            viewModel = viewModel
                        )
                    } else {
                        CRSendCryptoFlow(
                            step = viewModel.uiState.currentStep,
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }

    LoadingIndicator(viewModel.uiState.isLoading)
    BackHandler {
        viewModel.onUIEvent(CryptoSendSharedViewModel.UIEvent.OnCloseClick)
    }

    if (viewModel.uiState.openDialog.isActive.value) {
        CustomDialog(
            title = stringResource(id = viewModel.uiState.openDialog.titleResource),
            message = viewModel.uiState.openDialog.description.ifBlank {
                stringResource(viewModel.uiState.openDialog.descriptionResource)
            },
            positiveButtonText = stringResource(id = viewModel.uiState.openDialog.positiveResource),
            negativeButtonText = stringResource(id = viewModel.uiState.openDialog.negativeResource),
            openDialogCustom = viewModel.uiState.openDialog.isActive,
            onPositiveAction = viewModel.uiState.openDialog.positiveAction
        )
    }
}

@Composable
fun CRSendCryptoDirectFlow(step: Int, viewModel: CryptoSendSharedViewModel) {
    when (step) {
        CryptoSendSteps.One.pageNumber -> TemporalScreen(text = "Crypto Address Screen")
        CryptoSendSteps.Two.pageNumber -> { /* TODO: Send Amount Screen */ }
        CryptoSendSteps.Three.pageNumber -> { /* TODO: Voucher Screen */ }
    }
}

@Composable
fun CRSendCryptoFlow(step: Int, viewModel: CryptoSendSharedViewModel) {
    when (step) {
        CryptoSendSteps.One.pageNumber -> CryptoSendListOfCurrenciesScreen(sharedViewModel = viewModel)
        CryptoSendSteps.Two.pageNumber -> TemporalScreen(text = "Crypto Address Screen")
        CryptoSendSteps.Three.pageNumber -> { /* TODO: Send Amount Screen */ }
        CryptoSendSteps.Four.pageNumber -> { /* TODO: Voucher Screen */ }
    }
}

@Composable
fun TemporalScreen(text: String) {
    Text(
        text = text,
        style = Typography.h4.copy(fontWeight = FontWeight.W600),
        color = MultimoneyTheme.colors.text
    )
}
