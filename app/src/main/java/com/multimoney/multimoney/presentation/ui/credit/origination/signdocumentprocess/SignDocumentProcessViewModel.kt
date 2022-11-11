package com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.interaction.credit.SubscriptionCreditContractEventUseCase
import com.multimoney.domain.model.credit.CreditContractEvent
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.SIGN_DOCUMENT_ID_PRINT
import com.multimoney.multimoney.presentation.navigation.navgraph.SIGN_DOCUMENT_STEP
import com.multimoney.multimoney.presentation.navigation.navgraph.SIGN_DOCUMENT_URL
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.SignDocumentProcessViewModel.UIEvent.OnChangeScreen
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.SignDocumentProcessViewModel.UIEvent.OnCloseClick
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.SignDocumentProcessViewModel.UIEvent.OnInitializeText
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.SignDocumentProcessViewModel.UIEvent.OnNavigateToHome
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.SignDocumentProcessViewModel.UIEvent.OnRejectClick
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.catalog.SignDocumentStep.GENERATE_DOCUMENT_STEP
import com.multimoney.multimoney.presentation.util.catalog.SignDocumentStep.SIGN_DOCUMENTS_STEP
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class SignDocumentProcessViewModel @Inject constructor(
    private val subscriptionCreditContractEventUseCase: SubscriptionCreditContractEventUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel(true) {
    // uiState
    var uiState by mutableStateOf(UIState())
        private set
    var dialogDescription = ""
    var idBrand: Int = 0
    var idPrint: Long = 0
    var numAttemptsToStartSubscription: Int = 0

    init {
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        idPrint = savedStateHandle[SIGN_DOCUMENT_ID_PRINT] ?: 0
        uiState = uiState.copy(
            signDocumentProcessStep = savedStateHandle[SIGN_DOCUMENT_STEP] ?: "",
            signDocumentUrl = savedStateHandle[SIGN_DOCUMENT_URL] ?: ""
        )
        onListenCreditContractEventSubscription(idPrint, idBrand)
    }

    fun createDialog() {
        uiState = uiState.copy(
            dialogParameters = DialogParameters(
                titleResource = string.sign_credit_dialog_title,
                description = dialogDescription,
                positiveResource = string.sign_credit_dialog_continue,
                isActive = mutableStateOf(true)
            )
        )
    }

    private fun onListenCreditContractEventSubscription(idPrint: Long, idBrand: Int) {
        executeUseCase {
            subscriptionCreditContractEventUseCase.invoke(idPrint, idBrand).collectLatest { result ->
                result.onSuccess {
                    handleEvents(creditContractEvent = it)
                }.onFailure {
                    while (numAttemptsToStartSubscription < MAX_NUMBER_ATTEMPTS_TO_START_SUBSCRIPTION) {
                        onListenCreditContractEventSubscription(idPrint, idBrand)
                        numAttemptsToStartSubscription++
                    }
                }
            }
        }
    }

    private fun handleEvents(creditContractEvent: CreditContractEvent?) {
        when (uiState.signDocumentProcessStep) {
            GENERATE_DOCUMENT_STEP.value -> {
                if (creditContractEvent?.link.isNullOrEmpty().not()) {
                    uiState = uiState.copy(
                        signDocumentProcessStep = SIGN_DOCUMENT_STEP,
                        signDocumentUrl = creditContractEvent?.link ?: ""
                    )
                }
            }
            SIGN_DOCUMENTS_STEP.value -> {
                if (creditContractEvent?.statusOnfido != STATUS_FIRM_PENDING) {
                } else {
                }
            }
        }
    }

    private fun onRejectClick() {
        uiState = uiState.copy(isAlertResultVisible = true)
    }

    private fun onNavigateToHome() {
        popAndNavigateTo(
            route = Screen.HomeScreen.route,
            popTo = Screen.SignDocumentProcess.route
        )
    }

    data class UIState(
        // Interactions
        val signDocumentProcessStep: String = GENERATE_DOCUMENT_STEP.value,
        val dialogParameters: DialogParameters = DialogParameters(),
        val isAlertResultVisible: Boolean = false,
        val signDocumentUrl: String = ""
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnChangeScreen -> uiState = uiState.copy(signDocumentProcessStep = uiEvent.signDocumentStep)
            is OnInitializeText -> dialogDescription = uiEvent.dialogDescription
            is OnRejectClick -> onRejectClick()
            is OnCloseClick -> onNavigateToHome()
            is OnNavigateToHome -> onNavigateToHome()
        }
    }

    sealed class UIEvent {
        data class OnChangeScreen(val signDocumentStep: String) : UIEvent()
        data class OnInitializeText(val dialogDescription: String) : UIEvent()
        object OnRejectClick : UIEvent()
        object OnCloseClick : UIEvent()
        object OnNavigateToHome : UIEvent()
    }

    companion object {
        private const val MAX_NUMBER_ATTEMPTS_TO_START_SUBSCRIPTION = 3
        private const val STATUS_FIRM_PENDING = "Pendiente"
    }
}
