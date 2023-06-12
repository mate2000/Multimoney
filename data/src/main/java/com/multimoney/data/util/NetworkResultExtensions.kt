package com.multimoney.data.util

import com.apollographql.apollo3.ApolloCall
import com.apollographql.apollo3.api.Operation
import com.apollographql.apollo3.exception.ApolloException
import com.apollographql.apollo3.exception.ApolloParseException
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.util.MultimoneyResult.Failure
import com.multimoney.domain.model.util.MultimoneyResult.Success
import com.multimoney.domain.model.util.error.HttpError
import com.multimoney.domain.util.MultimoneyException.APOLLO_ERROR
import com.multimoney.domain.util.MultimoneyException.APOLLO_PARSE_EXCEPTION
import com.multimoney.domain.util.MultimoneyException.UNKNOWN_ERROR
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import java.io.IOException

inline fun <T, R> MultimoneyResult<T>.onMapping(action: (T) -> MultimoneyResult<R>): MultimoneyResult<R> {
    return when (this) {
        is MultimoneyResult.Success ->
            data.run(action)
        is MultimoneyResult.Failure ->
            this
        else -> MultimoneyResult.Loading(true)
    }
}

suspend fun <T : Operation.Data, U : Any?> fetchData(
    apolloCall: ApolloCall<T>,
    apolloCallMapper: suspend (T) -> MultimoneyResult<U>
): Flow<MultimoneyResult<U>> {
    return flow {
        when (val apolloResponse = invokeDataProvider(apolloCall)) {
            is Success -> {
                apolloResponse.data?.let {
                    emit(apolloCallMapper(it))
                }
            }
            is Failure -> {
                emit(Failure(apolloResponse.httpError))
            }
            else -> {} // NO-OP
        }
    }.onStart { emit(MultimoneyResult.Loading(true)) }
}

suspend fun <T : Operation.Data> invokeDataProvider(apolloCall: ApolloCall<T>) =
    try {
        withContext(Dispatchers.IO) {
            val apolloResponse = apolloCall.execute()
            if (apolloResponse.hasErrors()) {
                Failure(
                    HttpError(throwableList = apolloResponse.errors?.map { Throwable(it.message) })
                )
            } else {
                Success(apolloResponse.data)
            }
        }
    } catch (apolloException: ApolloException) {
        MultimoneyResult.Failure(
            HttpError(
                Throwable(
                    APOLLO_ERROR.description
                )
            )
        )
    } catch (e: ApolloParseException) {
        MultimoneyResult.Failure(
            HttpError(
                Throwable(
                    APOLLO_PARSE_EXCEPTION.description
                )
            )
        )
    } catch (e: IOException) {
        MultimoneyResult.Failure(
            HttpError(
                Throwable(
                    UNKNOWN_ERROR.description
                )
            )
        )
    }
