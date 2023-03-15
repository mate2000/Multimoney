package com.multimoney.multimoney.presentation.ui.smart.origination.sign

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.presentation.extension.findActivity
import com.multimoney.multimoney.presentation.ui.smart.origination.sign.SmartSignViewModel.BaseEvent.OpenWhatsAppLink
import com.multimoney.multimoney.presentation.ui.smart.origination.sign.SmartSignViewModel.BaseEvent.SimulateUserInteraction
import com.multimoney.multimoney.presentation.ui.smart.origination.sign.SmartSignViewModel.UIEvent
import com.multimoney.multimoney.presentation.ui.smart.origination.sign.SmartSignViewModel.UIEvent.OnCallCountryContact
import com.multimoney.multimoney.presentation.ui.smart.origination.sign.SmartSignViewModel.UIEvent.OnShowDialogInformation
import com.multimoney.multimoney.presentation.ui.smart.origination.sign.SmartSignViewModel.UIEvent.OnStartListenerSubscriptionSmartContractEvent
import com.multimoney.multimoney.presentation.uielement.AlertResult
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.util.MMCountDownTimer.OnCountDownTimerEvents
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.catalog.SignDocumentStep.GENERATE_DOCUMENT_STEP
import com.multimoney.multimoney.presentation.util.catalog.SignDocumentStep.SIGN_DOCUMENTS_STEP
import com.multimoney.multimoney.presentation.util.catalog.SignDocumentStep.VALIDATE_IDENTITY
import com.multimoney.multimoney.presentation.util.openWhatsAppDeepLink
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SmartSignScreen(
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: SmartSignViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = context.findActivity()


    LaunchedEffect(true) {
        viewModel.onUIEvent(OnCallCountryContact)
    }

    LaunchedEffect(true) {
        viewModel.apply {
            mmCountDownTimer.subscribe(object : OnCountDownTimerEvents {
                override fun onFinished() {
                    viewModel.onUIEvent(UIEvent.NavigateToSignUpDocument)
                }

                override fun onMaxTimeUsed(millisMainUntilFinished: Long) = Unit
            })
            onUIEvent(OnStartListenerSubscriptionSmartContractEvent)
            executeNavigation(onPopAndNavigate = onPopAndNavigate, onPopBackStack = onPopBackStack)
            baseEvent.collectLatest { event ->
                when (event) {
                    is SimulateUserInteraction -> activity?.onUserInteraction()
                    is OpenWhatsAppLink -> context.openWhatsAppDeepLink(event.whatsAppLink)
                }
            }
        }
    }

    when (viewModel.uiState.signDocumentProcessStep) {
        GENERATE_DOCUMENT_STEP.value -> {
            SmartDocumentGenerationScreen(
                viewModel = viewModel,
                icon = viewModel.uiState.loadingIcon,
                title = viewModel.uiState.loadingTitle,
                subtitle = viewModel.uiState.loadingSubtitle
            )
        }
        SIGN_DOCUMENTS_STEP.value -> {
            SmartSignDocumentScreen(viewModel = viewModel)
            LaunchedEffect(key1 = true) {
                viewModel.onUIEvent(OnShowDialogInformation)
            }
        }
        VALIDATE_IDENTITY.value -> {
            SmartValidateIdentityScreen(viewModel = viewModel)
        }
    }

    if (viewModel.uiState.isAlertResultVisible) {
        viewModel.uiState.apply {
            AlertResult(
                iconResource = alertResultIconResource,
                titleResource = alertResultTitleResource,
                descriptionResource = alertResultDescriptionResource,
                buttonTextResource = alertResultButtonResource,
                isTopNavBarVisible = true,
                isRightButtonVisible = alertResultIsRightButtonVisible,
                isLeftButtonVisible = alertResultIsLeftButtonVisible,
                onRightButtonClick = alertResultRightButtonClick,
                onButtonClick = alertResultButtonAction
            )
        }
    }

    if (viewModel.uiState.dialogParameters.isActive.value) {
        CustomDialog(
            title = stringResource(id = viewModel.uiState.dialogParameters.titleResource),
            message = viewModel.uiState.dialogParameters.description,
            positiveButtonText = stringResource(id = viewModel.uiState.dialogParameters.positiveResource),
            negativeButtonText = stringResource(id = viewModel.uiState.dialogParameters.negativeResource),
            openDialogCustom = viewModel.uiState.dialogParameters.isActive,
            onPositiveAction = viewModel.uiState.dialogParameters.positiveAction,
            onNegativeAction = viewModel.uiState.dialogParameters.negativeAction,
            isCancelable = false
        )
    }

    // this is require to block the onBack event
    BackHandler(onBack = {})
}
