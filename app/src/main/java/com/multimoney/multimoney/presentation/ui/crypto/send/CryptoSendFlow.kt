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

    LaunchedEffect(key1 = true) {
        when (viewModel.idBrand) {
            Brand.ElSalvador.id -> {
                /* Sending crypto is temporarily disabled to El Salvador. */
            }
            Brand.CostaRica.id -> {
                val step: Int = if (viewModel.comingFromCurrencyDetails) {
                    // If the user comes from the currency details screen, we start
                    // the flow on the Crypto Address Screen(Second Step)
                    CryptoSendSteps.Two.id
                } else {
                    CryptoSendSteps.One.id
                }
                viewModel.onUIEvent(CryptoSendSharedViewModel.UIEvent.SetCurrentStep(step))
            }
        }
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
            CRCryptoSendFlow(viewModel.uiState.currentStep, viewModel)
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
fun CRCryptoSendFlow(step: Int, viewModel: CryptoSendSharedViewModel) {
    when (step) {
        CryptoSendSteps.One.id -> {
            CryptoSendListOfCurrenciesScreen(sharedViewModel = viewModel)
        }
        CryptoSendSteps.Two.id -> {
            /* TODO: Crypto Address Screen */
            TemporalScreen(text = "Crypto Address Screen")
        }
        CryptoSendSteps.Three.id -> {
            /* TODO: Send Amount Screen */
        }
        CryptoSendSteps.Four.id -> {
            /* TODO: Voucher Screen */
        }
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
