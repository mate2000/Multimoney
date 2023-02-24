package com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess

import com.multimoney.domain.interaction.credit.SubscriptionCreditContractEventUseCase
import com.multimoney.domain.model.credit.CreditContractEvent
import com.multimoney.domain.model.util.error.HttpError
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.util.catalog.CreditSubscriptionStep
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

class CreditSubscriptionManager(var subscriptionCreditContractEventUseCase: SubscriptionCreditContractEventUseCase) {

    private var scope: CoroutineScope? = null
    private var numAttemptsToStartSubscription: Int = 1
    private var listener: SubscriptionEventListener? = null
    private var evicertiaLink: String? = ""
    var isSubcriptionRunning: Boolean = true

    fun startCreditSubscription(idBrand: Int, idPrint: Long) {
        scope = CoroutineScope(Dispatchers.IO + Job())
        scope?.launch {
            subscriptionCreditContractEventUseCase.invoke(idPrint, idBrand)
                .collectLatest { result ->
                    result.onSuccess {
                        Timber.d(SignDocumentProcessViewModel.LOG_SUBSCRIPTION_TAG, it?.currentStep)
                        if (it?.currentStep == CreditSubscriptionStep.LinkGenerated.step) {
                            evicertiaLink = it.link
                        }
                        listener?.onCapturedEvent(it)
                    }.onFailure {
                        if (numAttemptsToStartSubscription < MAX_NUMBER_ATTEMPTS_TO_START_SUBSCRIPTION) {
                            startCreditSubscription(idBrand, idPrint)
                            numAttemptsToStartSubscription++
                        } else {
                            isSubcriptionRunning = false
                            listener?.onSubscriptionFailToConnect(it)
                        }
                    }
                }
        }
    }

    fun subscriptionSubscribe(listener: SubscriptionEventListener) {
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
        fun onCapturedEvent(creditContractEvent: CreditContractEvent?)
        fun onSubscriptionFailToConnect(httpError: HttpError)
    }

    companion object {
        const val MAX_NUMBER_ATTEMPTS_TO_START_SUBSCRIPTION = 3
    }
}