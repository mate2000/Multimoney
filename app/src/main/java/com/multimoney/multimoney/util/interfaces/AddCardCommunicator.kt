package com.multimoney.multimoney.util.interfaces

import kotlinx.coroutines.flow.Flow

interface AddCardCommunicator {
    fun getFlowAddCardResult(): Flow<String>
}