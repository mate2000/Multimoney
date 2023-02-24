package com.multimoney.data.base

import com.apollographql.apollo3.ApolloCall
import com.apollographql.apollo3.api.Operation
import com.apollographql.apollo3.exception.ApolloException
import com.apollographql.apollo3.exception.ApolloParseException
import com.multimoney.data.database.util.DbConstants.NoResults
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.util.error.HttpError
import com.multimoney.domain.util.MultimoneyException.APOLLO_ERROR
import com.multimoney.domain.util.MultimoneyException.APOLLO_PARSE_EXCEPTION
import com.multimoney.domain.util.MultimoneyException.UNKNOWN_ERROR
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

abstract class BaseRepository {

    /**
     * Use this when communicating only with the api service
     */
    protected suspend fun <T : Operation.Data, U : Any?> fetchData(
        apolloCall: ApolloCall<T>,
        apolloCallMapper: suspend (T) -> MultimoneyResult<U>
    ): Flow<MultimoneyResult<U>> {
        return flow {
            when (val apolloResponse = invokeDataProvider(apolloCall)) {
                is MultimoneyResult.Success -> {
                    apolloResponse.data?.let {
                        emit(apolloCallMapper(it))
                    }
                }
                is MultimoneyResult.Failure -> {
                    emit(MultimoneyResult.Failure(apolloResponse.httpError))
                }
                else -> {} // NO-OP
            }
        }.onStart { emit(MultimoneyResult.Loading(true)) }
    }

    /**
     * Use this when communicating only with the api service
     */
    protected suspend fun <T : Operation.Data, U : Any?> fetchSubscription(
        apolloCall: ApolloCall<T>,
        apolloCallMapper: suspend (T?) -> MultimoneyResult<U?>
    ): Flow<MultimoneyResult<U?>> {
        return channelFlow {
            invokeSubscriptionProvider(apolloCall).collectLatest { multimoneyResult ->
                when (multimoneyResult) {
                    is MultimoneyResult.Success -> {
                        send(apolloCallMapper(multimoneyResult.data))
                    }
                    is MultimoneyResult.Failure -> {
                        send(MultimoneyResult.Failure(multimoneyResult.httpError))
                    }
                    else -> {} // NO-OP
                }
            }
        }.onStart { emit(MultimoneyResult.Loading(true)) }
    }

    /**
     * Use this if you need to cache data after fetching it from the api,
     * or retrieve something from cache
     */
    protected suspend fun <T : Operation.Data, U : Any?, V : DomainMapper<U>> fetchData(
        apolloCall: ApolloCall<T>,
        apolloCallMapper: suspend (T) -> MultimoneyResult<U>,
        dbSaveAction: suspend (T) -> Unit?,
        dbDataProvider: suspend () -> V?,
        forceLoadFromCache: Boolean = false
    ): Flow<MultimoneyResult<U>> {
        return flow {
            if (forceLoadFromCache) {
                dbDataProvider()?.let {
                    emit(MultimoneyResult.Success(it.mapToDomainModel()))
                }
                    ?: emit(MultimoneyResult.Failure(HttpError(Throwable(NoResults.message))))
            } else {
                when (val apolloResponse = invokeDataProvider(apolloCall)) {
                    is MultimoneyResult.Success -> {
                        apolloResponse.data?.let {
                            emit(apolloCallMapper(it))
                            dbSaveAction(it)
                        }
                    }
                    is MultimoneyResult.Failure -> {
                        dbDataProvider()?.let {
                            emit(MultimoneyResult.Success(it.mapToDomainModel()))
                        } ?: emit(MultimoneyResult.Failure(apolloResponse.httpError))
                    }
                    else -> {} // NO-OP
                }
            }
        }.onStart { emit(MultimoneyResult.Loading(true)) }
    }

    private suspend fun <T : Operation.Data> invokeSubscriptionProvider(apolloCall: ApolloCall<T>): Flow<MultimoneyResult<T?>> {
        return channelFlow {
            try {
                apolloCall.toFlow().collectLatest { apolloResponse ->
                    if (apolloResponse.hasErrors()) {
                        send(
                            MultimoneyResult.Failure(
                                HttpError(throwableList = apolloResponse.errors?.map { Throwable(it.message) })
                            )
                        )
                    } else {
                        send(MultimoneyResult.Success(apolloResponse.data))
                    }
                }
            } catch (apolloException: ApolloException) {
                send(
                    MultimoneyResult.Failure(
                        HttpError(
                            Throwable(
                                APOLLO_ERROR.description
                            )
                        )
                    )
                )
            } catch (e: ApolloParseException) {
                send(
                    MultimoneyResult.Failure(
                        HttpError(
                            Throwable(
                                APOLLO_PARSE_EXCEPTION.description
                            )
                        )
                    )
                )
            } catch (e: IOException) {
                send(
                    MultimoneyResult.Failure(
                        HttpError(
                            Throwable(
                                UNKNOWN_ERROR.description
                            )
                        )
                    )
                )
            }
        }
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
}
