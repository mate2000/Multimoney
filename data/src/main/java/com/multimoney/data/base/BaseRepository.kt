package com.multimoney.data.base

import com.apollographql.apollo3.ApolloCall
import com.apollographql.apollo3.api.Operation
import com.apollographql.apollo3.exception.ApolloException
import com.apollographql.apollo3.exception.ApolloParseException
import com.multimoney.data.database.util.DbConstants
import com.multimoney.domain.model.util.HttpError
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.util.MultimoneyException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import java.io.IOException

abstract class BaseRepository {

    /**
     * Use this when communicating only with the api service
     */
    protected suspend fun <T : Operation.Data, U : Any?> fetchData(
        apolloCall: ApolloCall<T>,
        apolloCallMapper: suspend (T) -> U
    ): Flow<MultimoneyResult<U>> {
        return flow {
            when (val apolloResponse = invokeDataProvider(apolloCall)) {
                is MultimoneyResult.Success -> {
                    apolloResponse.data?.let {
                        emit(MultimoneyResult.Success(apolloCallMapper(it)))
                    }
                }
                is MultimoneyResult.Failure -> {
                    emit(MultimoneyResult.Failure(apolloResponse.httpError))
                }
                else -> {}// NO-OP
            }
        }.onStart { emit(MultimoneyResult.Loading(true)) }
    }

    /**
     * Use this if you need to cache data after fetching it from the api,
     * or retrieve something from cache
     */
    protected suspend fun <T : Operation.Data, U : Any?, V : DomainMapper<U>> fetchData(
        apolloCall: ApolloCall<T>,
        apolloCallMapper: suspend (T) -> U,
        dbSaveAction: suspend (T) -> Unit?,
        dbDataProvider: suspend () -> V?,
        forceLoadFromCache: Boolean = false
    ): Flow<MultimoneyResult<U>> {
        return flow {
            if (forceLoadFromCache) {
                dbDataProvider()?.let {
                    emit(MultimoneyResult.Success(it.mapToDomainModel()))
                }
                    ?: emit(MultimoneyResult.Failure(HttpError(Throwable(DbConstants.NoResults.message))))
            } else {
                when (val apolloResponse = invokeDataProvider(apolloCall)) {
                    is MultimoneyResult.Success -> {
                        apolloResponse.data?.let {
                            emit(MultimoneyResult.Success(apolloCallMapper(it)))
                            dbSaveAction(it)
                        }
                    }
                    is MultimoneyResult.Failure -> {
                        dbDataProvider()?.let {
                            emit(MultimoneyResult.Success(it.mapToDomainModel()))
                        } ?: emit(MultimoneyResult.Failure(apolloResponse.httpError))
                    }
                    else -> {}// NO-OP
                }
            }
        }.onStart { emit(MultimoneyResult.Loading(true)) }
    }

    private suspend fun <T : Operation.Data> invokeDataProvider(apolloCall: ApolloCall<T>) =
        try {
            withContext(Dispatchers.IO) {
                val apolloResponse = apolloCall.execute()
                if (apolloResponse.hasErrors()) {
                    MultimoneyResult.Failure(
                        HttpError(throwableList = apolloResponse.errors?.map { Throwable(it.message) })
                    )
                } else {
                    MultimoneyResult.Success(apolloResponse.data)
                }
            }
        } catch (apolloException: ApolloException) {
            MultimoneyResult.Failure(
                HttpError(
                    Throwable(
                        MultimoneyException.APOLLO_ERROR.description
                    )
                )
            )
        } catch (e: ApolloParseException) {
            MultimoneyResult.Failure(
                HttpError(
                    Throwable(
                        MultimoneyException.APOLLO_PARSE_EXCEPTION.description
                    )
                )
            )
        } catch (e: IOException) {
            MultimoneyResult.Failure(
                HttpError(
                    Throwable(
                        MultimoneyException.UNKNOWN_ERROR.description
                    )
                )
            )
        }
}