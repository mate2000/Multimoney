package com.multimoney.data.base

import android.util.Log
import com.apollographql.apollo3.ApolloCall
import com.apollographql.apollo3.api.Operation
import com.apollographql.apollo3.exception.ApolloException
import com.apollographql.apollo3.exception.ApolloParseException
import com.multimoney.data.database.util.DB_ENTRY_ERROR
import com.multimoney.data.util.exeption.DataSourceException
import com.multimoney.data.util.exeption.MultimoneyException
import com.multimoney.domain.model.util.HttpError
import com.multimoney.domain.model.util.MultimoneyResult
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
    protected suspend fun <T : Operation.Data> fetchData(apolloCall: ApolloCall<T>): Flow<MultimoneyResult<T>> {
        return flow {
            when (val apolloResponse = invokeDataProvider(apolloCall)) {
                is MultimoneyResult.Success -> {
                    apolloResponse.data?.let {
                        emit(MultimoneyResult.Success(it))
                    }
                }
                is MultimoneyResult.Failure -> {
                    emit(MultimoneyResult.Failure(apolloResponse.httpError))
                }
            }
        }.onStart { emit(MultimoneyResult.Loading) }
    }

    /**
     * Use this if you need to cache data after fetching it from the api,
     * or retrieve something from cache
     */
    protected suspend fun <T : Operation.Data, R : DomainMapper<T>> fetchData(
        apolloCall: ApolloCall<T>,
        dbSaveAction: () -> T?,
        dbDataProvider: suspend () -> R?,
        forceLoadFromCache: Boolean = false
    ): Flow<MultimoneyResult<T>> {
        return flow {
            if (forceLoadFromCache) {
                dbDataProvider()?.let {
                    emit(MultimoneyResult.Success(it.mapToDomainModel()))
                } ?: emit(MultimoneyResult.Failure(HttpError(Throwable(DB_ENTRY_ERROR))))
            } else {
                when (val apolloResponse = invokeDataProvider(apolloCall)) {
                    is MultimoneyResult.Success -> {
                        apolloResponse.data?.let {
                            emit(MultimoneyResult.Success(it))
                            dbSaveAction
                        }
                    }
                    is MultimoneyResult.Failure -> {
                        dbDataProvider()?.let {
                            emit(MultimoneyResult.Success(it.mapToDomainModel()))
                        } ?: emit(MultimoneyResult.Failure(apolloResponse.httpError))
                    }
                }
            }
        }.onStart { emit(MultimoneyResult.Loading) }
    }

    private suspend fun <T : Operation.Data> invokeDataProvider(apolloCall: ApolloCall<T>) =
        try {
            withContext(Dispatchers.IO) {
                val apolloResponse = apolloCall.execute()
                apolloResponse.hasErrors()
                if (apolloResponse.hasErrors()) {
                    MultimoneyResult.Failure(
                        HttpError(
                            Throwable(
                                DataSourceException.Server(
                                    apolloResponse.errors?.first()
                                ).message
                            )
                        )
                    )
                } else {
                    MultimoneyResult.Success(apolloResponse.data)
                }
            }
        } catch (apolloException: ApolloException) {
            Log.w(
                MultimoneyException.APOLLO_ERROR.name,
                MultimoneyException.APOLLO_ERROR.description
            )
            MultimoneyResult.Failure(
                HttpError(
                    Throwable(
                        DataSourceException.Unexpected(
                            MultimoneyException.APOLLO_ERROR
                        )
                    )
                )
            )
        } catch (e: ApolloParseException) {
            Log.w(
                MultimoneyException.APOLLO_PARSE_EXCEPTION.name,
                MultimoneyException.APOLLO_PARSE_EXCEPTION.description
            )
            MultimoneyResult.Failure(
                HttpError(
                    Throwable(
                        DataSourceException.Unexpected(
                            MultimoneyException.APOLLO_PARSE_EXCEPTION
                        )
                    )
                )
            )
        } catch (e: IOException) {
            Log.w(
                MultimoneyException.UNKNOWN_ERROR.name,
                MultimoneyException.UNKNOWN_ERROR.description
            )
            MultimoneyResult.Failure(
                HttpError(
                    Throwable(
                        DataSourceException.Unexpected(
                            MultimoneyException.UNKNOWN_ERROR
                        )
                    )
                )
            )
        }
}