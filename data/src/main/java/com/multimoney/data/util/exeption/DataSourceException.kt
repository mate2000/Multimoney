package com.multimoney.data.util.exeption

import com.apollographql.apollo3.api.Error

sealed class DataSourceException(
    val exception: Any?
) : RuntimeException() {
    class Unexpected(exception: MultimoneyException) : DataSourceException(exception)
    class Server(error: Error?) : DataSourceException(error)
}
