package com.multimoney.multimoney.presentation.util

import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import kotlinx.coroutines.flow.Flow

interface SignOutCommunicator {
    fun onMaxTimeUsedDialogChangeState(dialogParameters: DialogParameters)
    fun isAppInForeground(): Boolean
    fun isSessionDuplicated(): Flow<Boolean>
}
