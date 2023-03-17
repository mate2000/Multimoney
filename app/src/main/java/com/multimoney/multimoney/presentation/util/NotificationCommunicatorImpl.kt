package com.multimoney.multimoney.presentation.util

import javax.inject.Inject

class NotificationCommunicatorImpl @Inject constructor() : NotificationCommunicator {

    override fun getNavigateToRoute(route: String, action: (String) -> Unit) {
        action.invoke(route)
    }
}
