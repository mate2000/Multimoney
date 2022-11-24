package com.multimoney.multimoney.presentation.ui.smart.origination.evicertia

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.data.util.catalog.CreditOnFidoOrFirmStatus
import com.multimoney.domain.interaction.accountsmart.SubscriptionAccountSmartContractUseCase
import com.multimoney.domain.model.accountsmart.AccountSmartContractResult
import com.multimoney.domain.model.credit.CreditContractEvent
import com.multimoney.domain.model.util.error.HttpError
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.SignDocumentProcessViewModel
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.SignDocumentProcessViewModel.BaseEvent.SimulateUserInteraction
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.SignDocumentProcessViewModel.Companion.MAX_NUMBER_ATTEMPTS_TO_START_SUBSCRIPTION
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.catalog.OnfidoAndEvicertiaError.EVICERTIA_REJECTED_FIRST_TIME
import com.multimoney.multimoney.presentation.util.catalog.OnfidoAndEvicertiaError.EVICERTIA_REJECTED_SECOND_TIME
import com.multimoney.multimoney.presentation.util.catalog.OnfidoAndEvicertiaError.ONFIDO_REJECTED_FIRST_TIME
import com.multimoney.multimoney.presentation.util.catalog.OnfidoAndEvicertiaError.ONFIDO_REJECTED_SECOND_TIME
import com.multimoney.multimoney.presentation.util.catalog.SignDocumentStep.GENERATE_DOCUMENT_STEP
import com.multimoney.multimoney.presentation.util.catalog.SignDocumentStep.SIGN_DOCUMENTS_STEP
import com.multimoney.multimoney.presentation.util.catalog.SignDocumentStep.VALIDATE_IDENTITY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class SmartEvicertiaViewModel @Inject constructor(private val subscriptionAccountSmartContractUseCase: SubscriptionAccountSmartContractUseCase) :
    BaseViewModel(true) {

    var uiState by mutableStateOf(UIState())
        private set

    var numAttemptsToStartSubscription: Int = 0

    private fun onListenCreditContractEventSubscription(idBrand: Int, idRequestSys: Long) {
        executeUseCase {
            subscriptionAccountSmartContractUseCase.invoke(idBrand, idRequestSys)
                .collectLatest { result ->
                    result.onSuccess {
                        handleEvents(creditContractEvent = it)
                    }.onFailure {
                        while (numAttemptsToStartSubscription < MAX_NUMBER_ATTEMPTS_TO_START_SUBSCRIPTION) {
                            onListenCreditContractEventSubscription(idBrand, idRequestSys)
                            numAttemptsToStartSubscription++
                        }
                    }
                }
        }
    }

    private fun handleEvents(creditContractEvent: AccountSmartContractResult?) {
        when (uiState.signDocumentProcessStep) {
            GENERATE_DOCUMENT_STEP.value -> {
                if (creditContractEvent?.link.isNullOrEmpty().not()) {
                    emitBaseEvent(SimulateUserInteraction)
                    uiState = uiState.copy(
                        signDocumentProcessStep = SIGN_DOCUMENTS_STEP.value,
                        signDocumentUrl = creditContractEvent?.link ?: ""
                    )
                }
            }
            SIGN_DOCUMENTS_STEP.value -> {
                when (creditContractEvent?.statusEvicertia?.lowercase()) {
                    CreditOnFidoOrFirmStatus.FIRMED.status.lowercase() -> {
                        handleOnfidoStatus(creditContractEvent)
                    }
                    CreditOnFidoOrFirmStatus.REJECTED.status.lowercase() -> {
                        emitBaseEvent(SimulateUserInteraction)
                        onNavigateToOnfidoAndEvicertiaError(EVICERTIA_REJECTED_FIRST_TIME.value)
                    }
                    CreditOnFidoOrFirmStatus.OVER_COUNTER.status.lowercase() -> {
                        emitBaseEvent(SimulateUserInteraction)
                        onNavigateToOnfidoAndEvicertiaError(EVICERTIA_REJECTED_SECOND_TIME.value)
                    }
                }
            }
            VALIDATE_IDENTITY.value -> {
                emitBaseEvent(SimulateUserInteraction)
                handleOnfidoStatus(creditContractEvent)
            }
        }
    }

    private fun handleOnfidoStatus(
        creditContractEvent: CreditContractEvent?
    ) {
        emitBaseEvent(SimulateUserInteraction)
        when (creditContractEvent?.statusOnfido?.lowercase()) {
            CreditOnFidoOrFirmStatus.PENDING.status.lowercase() -> {
                uiState = uiState.copy(
                    signDocumentProcessStep = VALIDATE_IDENTITY.value
                )
            }
            CreditOnFidoOrFirmStatus.APPROVED.status.lowercase() -> {
                popAndNavigateTo(
                    route = Screen.ProcessingTransactionScreen.route,
                    popTo = Screen.SignDocumentProcessScreen.route
                )
            }
            CreditOnFidoOrFirmStatus.REJECTED.status.lowercase() -> {
                onNavigateToOnfidoAndEvicertiaError(ONFIDO_REJECTED_FIRST_TIME.value)
            }
            CreditOnFidoOrFirmStatus.OVER_COUNTER.status.lowercase() -> {
                onNavigateToOnfidoAndEvicertiaError(ONFIDO_REJECTED_SECOND_TIME.value)
            }
        }
    }

    private fun onNavigateToOnfidoAndEvicertiaError(error: String) {
        popAndNavigateTo(
            route = "${Screen.OnfidoAndEvicertiaErrorsScreen.baseRoute}/$error/$idBrand/$pkUser/$identification/$email/$idUserRequest/$firstName/$lastName",
            popTo = Screen.SignDocumentProcessScreen.route
        )
    }

    private fun onFailure(error: HttpError) {
        uiState = uiState.copy(isLoading = false)
        openDialog = DialogParameters(
            description = error.getError() ?: "",
            isActive = mutableStateOf(true)
        )
    }

    data class UIState(
        // Fields
        val html: String = "",
        var isLoading: Boolean = false,
        val openDialog: DialogParameters = DialogParameters()
    )
}