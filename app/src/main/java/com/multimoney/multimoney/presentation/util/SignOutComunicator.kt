package com.multimoney.multimoney.presentation.util

import com.multimoney.multimoney.presentation.util.catalog.DialogParameters

interface SignOutCommunicator {
    fun onMaxTimeUsedDialogChangeState(dialogParameters: DialogParameters)
    fun isAppInForeground(): Boolean
}
