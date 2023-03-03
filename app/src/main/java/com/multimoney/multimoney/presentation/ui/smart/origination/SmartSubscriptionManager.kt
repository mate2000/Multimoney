package com.multimoney.multimoney.presentation.ui.smart.origination

import com.multimoney.domain.interaction.accountsmart.MutationAccountStatusUseCase
import com.multimoney.domain.interaction.accountsmart.SubscriptionAccountSmartContractUseCase
import com.multimoney.domain.model.accountsmart.AccountSmartContractResult
import com.multimoney.domain.model.util.error.HttpError
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.util.catalog.CreditSubscriptionStep
import com.onfido.api.client.Utils.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

class SmartSubscriptionManager(var subscriptionSmartContractEventUseCase: SubscriptionAccountSmartContractUseCase) {

    private var scope: CoroutineScope? = null
    private var numAttemptsToStartSubscription: Int = 1
    private var listener: SubscriptionEventListener? = null
    private var evicertiaLink: String? = ""
    var isSubcriptionRunning: Boolean = true

    fun startSmartSubscription(idSysRequest: Long, idBrand: Int) {
        scope = CoroutineScope(Dispatchers.IO + Job())
        scope?.launch {
            subscriptionSmartContractEventUseCase.invoke(idSysRequest, idBrand)
                .collectLatest { result ->
                    result.onSuccess { smartContract ->
                        listener?.onCapturedEvent(smartContract)
                        if (smartContract?.currentStep == CreditSubscriptionStep.LinkGenerated.step) {
                            evicertiaLink = smartContract.link
                        }
                    }.onFailure {
                        if (numAttemptsToStartSubscription < MAX_NUMBER_ATTEMPTS_TO_START_SUBSCRIPTION) {
                            startSmartSubscription(idSysRequest,idBrand)
                            numAttemptsToStartSubscription++
                        } else {
                            isSubcriptionRunning = false
                            listener?.onSubscriptionFailToConnect(it)
                        }
                    }
                }
        }
    }

    fun idSubscriptionSubscribe(listener: SubscriptionEventListener) {
        this.listener = listener
    }

    fun hasEvisertiaLink() = evicertiaLink.isNullOrEmpty().not()

    fun getEvisertioLink() = evicertiaLink

    fun cancelSubscription() {
        try {
            scope?.cancel()
        } catch (e: java.lang.IllegalStateException) {
            Timber.d(e.message)
        }
        evicertiaLink = ""
        isSubcriptionRunning = true
        numAttemptsToStartSubscription = 0
    }

    fun destroySubscription() {
        try {
            scope?.cancel()
        } catch (e: java.lang.IllegalStateException) {
            Timber.d(e.message)
        }
        evicertiaLink = ""
        isSubcriptionRunning = true
        numAttemptsToStartSubscription = 0
        listener = null
    }

    interface SubscriptionEventListener {
        fun onCapturedEvent(smartContractEvent: AccountSmartContractResult?)
        fun onSubscriptionFailToConnect(httpError: HttpError)
    }

    companion object {
        const val MAX_NUMBER_ATTEMPTS_TO_START_SUBSCRIPTION = 3
        const val LOG_SUBSCRIPTION_TAG = "MM_SUBSCRIPTION_M"
    }
}