package com.multimoney.multimoney.presentation.ui.test.subscription

import android.util.Log
import com.multimoney.domain.interaction.credit.MutationSendCreditContractEventUseCase
import com.multimoney.domain.interaction.credit.SubscriptionCreditContractEventUseCase
import com.multimoney.domain.model.credit.CreditContractEvent
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest

@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val mutationSendCreditContractEventUseCase: MutationSendCreditContractEventUseCase,
    private val creditContractEventUseCase: SubscriptionCreditContractEventUseCase
) : BaseViewModel(false) {

    fun sendCreditContractEvent(creditContractEvent: CreditContractEvent) {
        executeUseCase {
            mutationSendCreditContractEventUseCase.invoke(
                creditContractEvent.idPrint,
                creditContractEvent.idBrand ?: 0,
                creditContractEvent.link ?: "",
                creditContractEvent.active ?: false,
                creditContractEvent.statusEvicertia ?: "",
                creditContractEvent.statusOnfido ?: "",
                creditContractEvent.currentStep ?: ""
            ).collectLatest { result ->
                result.onSuccess {
                    Log.wtf("Subscription Diego", "Success")
                }
                result.onFailure { httpError ->
                    Log.wtf("Subscription Diego", httpError.getError())
                }
            }
        }
    }

    fun subscribe() {
        executeUseCase {
            creditContractEventUseCase.invoke(
                466503,
                10
            ).collectLatest { result ->
                result.onSuccess {
                    Log.wtf("Subscription", "Subscription connected")
                }.onFailure {
                    Log.wtf("Subscription", "Subscription error connection")
                }
            }
        }
    }
}
