package com.multimoney.multimoney.presentation.ui.smart.origination.util

import com.multimoney.domain.model.accountsmart.AccountSmartData
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel
import kotlinx.coroutines.flow.MutableSharedFlow

/**
 * in order to reuse the same logic, this function will be shared between all smart
 * origination screen children.
 */
suspend fun MutableSharedFlow<Any>.collectSmartStepByStepData(
    onEventCollected: (accountSmartData: AccountSmartData) -> Unit
) {
    this.collect { event ->
        when (event) {
            is SmartViewModel.BaseEvent.OnListStepByStepFetched -> {
                event.accountSmartData?.let { onEventCollected(it) }
            }
        }
    }
}
