package com.multimoney.domain.model.util

import com.multimoney.domain.util.MultimoneyException

class HttpError(
    val throwable: Throwable? = null,
    val throwableList: List<Throwable>? = null,
    val errorCode: Int = 0
) {
    fun getError() = when {
        throwable != null -> {
            throwable.message
        }
        throwableList != null -> {
            var errorMessages = ""
            throwableList.forEachIndexed { index, error ->
                errorMessages = if (index == 0) {
                    error.message.toString()
                } else {
                    "$errorMessages \n${error.message}"
                }
            }
            errorMessages
        }
        else -> {
            MultimoneyException.UNKNOWN_ERROR.description
        }
    }
}