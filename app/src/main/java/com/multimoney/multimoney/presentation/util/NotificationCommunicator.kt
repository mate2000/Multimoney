package com.multimoney.multimoney.presentation.util

interface NotificationCommunicator {
    fun getNavigateToRoute(route: String, action: (String) -> Unit)
}
