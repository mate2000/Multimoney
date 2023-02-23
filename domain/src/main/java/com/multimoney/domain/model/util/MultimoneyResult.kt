package com.multimoney.domain.model.util

import com.multimoney.domain.model.util.error.HttpError

sealed class MultimoneyResult<out T> {
    data class Success<out T>(val data: T) : MultimoneyResult<T>()
    data class Message<out T>(val message: T) : MultimoneyResult<T>()
    data class Failure(val httpError: HttpError) : MultimoneyResult<Nothing>()
    data class Loading(val isLoading: Boolean) : MultimoneyResult<Nothing>()
}

inline fun <T : Any?> MultimoneyResult<T>.onSuccess(action: (T) -> Unit): MultimoneyResult<T> {
    if (this is MultimoneyResult.Success) action(data)
    return this
}

inline fun <T : Any?> MultimoneyResult<T>.onMessage(action: (T) -> Unit): MultimoneyResult<T> {
    if (this is MultimoneyResult.Message) action(message)
    return this
}

inline fun <T : Any?> MultimoneyResult<T>.onFailure(action: (HttpError) -> Unit): MultimoneyResult<T> {
    if (this is MultimoneyResult.Failure) if (httpError.errorCode != 403) action(httpError)
    return this
}

inline fun <T : Any?> MultimoneyResult<T>.onLoading(action: () -> Unit): MultimoneyResult<T> {
    if (this is MultimoneyResult.Loading) action()
    return this
}
