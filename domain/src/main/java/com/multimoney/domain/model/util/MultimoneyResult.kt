package com.multimoney.domain.model.util

sealed class MultimoneyResult<out T> {
    data class Success<out T>(val data: T) : MultimoneyResult<T>()
    data class Failure(val httpError: HttpError) : MultimoneyResult<Nothing>()
    object Loading : MultimoneyResult<Nothing>()
}

inline fun <T : Any> MultimoneyResult<T>.onSuccess(action: (T) -> Unit): MultimoneyResult<T> {
    if (this is MultimoneyResult.Success) action(data)
    return this
}

inline fun <T : Any> MultimoneyResult<T>.onFailure(action: (HttpError) -> Unit): MultimoneyResult<T> {
    if (this is MultimoneyResult.Failure) action(httpError)
    return this
}

inline fun <T : Any> MultimoneyResult<T>.onLoading(action: () -> Unit): MultimoneyResult<T> {
    if (this is MultimoneyResult.Loading) action()
    return this
}
