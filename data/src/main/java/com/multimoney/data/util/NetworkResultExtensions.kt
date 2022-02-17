package com.multimoney.data.util

import com.multimoney.domain.model.util.MultimoneyResult

inline fun <T, R> MultimoneyResult<T>.onMapping(action: (T) -> MultimoneyResult<R>): MultimoneyResult<R> {
    return when (this) {
        is MultimoneyResult.Success ->
            data.run(action)
        is MultimoneyResult.Failure ->
            this
        else -> MultimoneyResult.Loading
    }
}