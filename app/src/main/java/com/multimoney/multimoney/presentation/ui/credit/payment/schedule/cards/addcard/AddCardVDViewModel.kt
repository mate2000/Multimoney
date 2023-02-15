package com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.addcard

import android.app.Activity
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.multimoney.domain.interaction.virtualcard.MutationCreateCardVDUseCase
import com.multimoney.domain.interaction.virtualcard.MutationCreateUserVDUseCase
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.navgraph.*
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardViewModel
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.addcard.AddCardVDViewModel.UIEvent.OnCallMutationCreateUserVDUseCase
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.addcard.AddCardVDViewModel.UIEvent.OnCallMutationCreateCardVDUseCase
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.addcard.AddCardVDViewModel.UIEvent.InitAddCardFlow
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.util.interfaces.AddCardCommunicator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddCardVDViewModel @Inject constructor(
    //val addCardResult: PackageTrackerModule,
    val savedStateHandle: SavedStateHandle,
    private val mutationCreateUserVDUseCase: MutationCreateUserVDUseCase,
    private val mutationCreateCardVDUseCase: MutationCreateCardVDUseCase
) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(PaymentScheduleCardViewModel.UIState())
        private set

    // Stateless
    private var idBrand: Int = 0
    private var identification: String = ""
    private var userName: String = ""
    private var email: String = ""
    private var visaDirectUser: String? = null
    private var firstName: String = ""
    private var secondName: String = ""
    private var lastName: String = ""
    private var secondLastName: String = ""
    private var callerId: String = ""
    private var accountToken: Int = 0
    private var addCardCommunicator: AddCardCommunicator? = null


    init {
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        firstName = savedStateHandle[FIRST_NAME] ?: ""
        secondName = savedStateHandle[SECOND_NAME] ?: ""
        lastName = savedStateHandle[LAST_NAME] ?: ""
        secondLastName = savedStateHandle[SECOND_LAST_NAME] ?: ""
        email = savedStateHandle[EMAIL] ?: ""
        callerId = savedStateHandle[CALLER_ID] ?: ""
        userName = savedStateHandle[USER] ?: ""
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        accountToken = savedStateHandle[ACCOUNT_TOKEN_CREDIT] ?: 0
        visaDirectUser = savedStateHandle[VISA_DIRECT_USER]
        if (visaDirectUser == null) {
            viewModelScope.launch {
                onCallMutationCreateUserVDUseCase()
            }
        }
    }

    private fun onCallMutationCreateUserVDUseCase() = executeUseCase {
        mutationCreateUserVDUseCase.invoke(
            identification =  identification,
            firstName = firstName,
            secondName = secondName,
            lastName = lastName,
            secondLastName = secondLastName,
            email = email,
            callerId = callerId,
            user = userName,
            idBrand = idBrand,
            accountToken = accountToken.toLong()
        ).collectLatest { result ->
            result.onSuccess {
                uiState = uiState.copy(isLoading = false)
            }.onFailure {
                uiState = uiState.copy(
                    isLoading = false,
                    openDialog = DialogParameters(
                        description = it.getError() ?: "",
                        isActive = mutableStateOf(true)
                    )
                )
            }.onLoading {
                uiState = uiState.copy(isLoading = true)
            }
        }
    }

    private fun onCallMutationCreateCardVDUseCase(cardTokenId: String) {
        Log.v("Citerio", cardTokenId)
    }

    /*private fun onCallMutationCreateCardVDUseCase(cardTokenId: String) = executeUseCase {
        mutationCreateCardVDUseCase.invoke(
            identification =  identification,
            cardTokenID = cardTokenId,
            default = true,
            user = userName,
            idBrand = idBrand
        ).collectLatest { result ->
            result.onSuccess {
                uiState = uiState.copy(isLoading = false)
            }.onFailure {
                uiState = uiState.copy(
                    isLoading = false,
                    openDialog = DialogParameters(
                        description = it.getError() ?: "",
                        isActive = mutableStateOf(true)
                    )
                )
            }.onLoading {
                uiState = uiState.copy(isLoading = true)
            }
        }
    }*/

    data class UIState(
        val isLoading: Boolean = false,
        val openDialog: DialogParameters = DialogParameters()
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnCallMutationCreateUserVDUseCase -> onCallMutationCreateUserVDUseCase()
            is OnCallMutationCreateCardVDUseCase -> onCallMutationCreateCardVDUseCase(uiEvent.cardTokenId)
            is InitAddCardFlow -> onInitAddCardFlow(uiEvent.activity)
        }
    }

    private fun onInitAddCardFlow(activity: Activity?) {
        activity?.let { safeActivity ->
            addCardCommunicator = safeActivity as AddCardCommunicator
            viewModelScope.launch {
                addCardCommunicator?.getFlowAddCardResult()?.collectLatest {
                    Log.v("CITERIO", it)
                }
            }
        }
    }

    sealed class UIEvent {
        object OnCallMutationCreateUserVDUseCase : UIEvent()
        data class OnCallMutationCreateCardVDUseCase(val cardTokenId: String) : UIEvent()
        data class InitAddCardFlow(val activity: Activity?) : UIEvent()
    }

    /*override fun onCallBackResult(response: String) {
        Log.v("CITERIO VM", response)
    }*/

    /*class VisaCardVDReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            val isAirplaneModeEnabled = intent?.getBooleanExtra("state", false) ?: return

        }
    }*/

}