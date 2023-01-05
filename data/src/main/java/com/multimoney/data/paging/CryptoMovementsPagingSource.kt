package com.multimoney.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.multimoney.data.mapper.crypto.mapToDomainModel
import com.multimoney.data.networking.GraphqlApi
import com.multimoney.data.util.fetchData
import com.multimoney.domain.model.crypto.CryptoCurrencyMovement
import com.multimoney.domain.model.util.MultimoneyResult.Success
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onSuccess
import kotlinx.coroutines.flow.collectLatest

class CryptoMovementsPagingSource(
    private val graphqlApi: GraphqlApi,
    private val user: String,
    private val idBrand: Int,
    private val identification: String,
    private val market: String,
    private val order_time_begin: Any,
    private val order_time_end: Any,
    private val pageSize: Int
) : PagingSource<Int, CryptoCurrencyMovement>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CryptoCurrencyMovement> {
        val currentPage = params.key ?: INDEX_ONE
        return try {
            val response = fetchData(
                apolloCall = graphqlApi.queryGetCryptoCurrencyMovements(
                    user,
                    idBrand,
                    identification,
                    market,
                    order_time_begin,
                    order_time_end,
                    pageSize,
                    currentPage
                ),
                apolloCallMapper = { data -> Success(data.mapToDomainModel()) }
            )

            var searchResult = emptyList<CryptoCurrencyMovement>()
            val cryptoMovementsResult = mutableListOf<CryptoCurrencyMovement>()
            var endOfPageReached = false

            response.collectLatest { result ->
                result.onSuccess { cryptoCurrencyMovements ->
                    cryptoCurrencyMovements.cryptoCurrencyMovements.items.forEach { cryptoMovement ->
                            cryptoMovementsResult.add(cryptoMovement)
                    }
                    searchResult = cryptoMovementsResult
                    endOfPageReached =
                        pageSize * currentPage > cryptoCurrencyMovements.cryptoCurrencyMovements.total_count
                }
                result.onFailure { error ->
                    error.throwable?.let { LoadResult.Error<Int, CryptoCurrencyMovement>(it) }
                }
            }

            if (searchResult.isNotEmpty()) {
                LoadResult.Page(
                    data = searchResult,
                    prevKey = if (currentPage == INDEX_ONE) null else currentPage.minus(INDEX_ONE),
                    nextKey = if (endOfPageReached) null else currentPage.plus(INDEX_ONE)
                )
            } else {
                LoadResult.Page(
                    data = emptyList(),
                    prevKey = null,
                    nextKey = null
                )
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, CryptoCurrencyMovement>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(INDEX_ONE)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(INDEX_ONE)
        }
    }

    companion object {
        const val INDEX_ONE = 1
    }
}
