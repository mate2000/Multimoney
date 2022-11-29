package com.multimoney.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.multimoney.data.mapper.smartaccount.mapToDomainModel
import com.multimoney.data.networking.GraphqlApi
import com.multimoney.data.util.fetchData
import com.multimoney.domain.model.accountsmart.SmartMovement
import com.multimoney.domain.model.util.MultimoneyResult.Success
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onSuccess
import kotlinx.coroutines.flow.collectLatest

class SmartMovementsPagingSource(
    private val graphqlApi: GraphqlApi,
    private val user: String,
    private val idBrand: Int,
    private val identificationNumber: String,
    private val accountToken: Long,
    private val monthDate: String?
) : PagingSource<Int, SmartMovement>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SmartMovement> {
        val currentPage = params.key ?: INDEX_ONE
        return try {
            val response = fetchData(
                apolloCall = graphqlApi.queryGetCoreBankMovements(
                    user,
                    idBrand,
                    identificationNumber,
                    accountToken,
                    currentPage,
                    PAGE_SIZE,
                    monthDate
                ),
                apolloCallMapper = { data ->
                    Success(data.mapToDomainModel())
                }
            )

            var searchResult = emptyList<SmartMovement>()
            var endOfPageReached = false

            response.collectLatest { result ->
                result.onSuccess { moves ->
                    searchResult = moves?.result ?: emptyList()
                    endOfPageReached = PAGE_SIZE * currentPage > (moves?.totalRecords ?: 0)
                }
                result.onFailure { error ->
                    error.throwable?.let { LoadResult.Error<Int, SmartMovement>(it) }
                }
            }

            if (searchResult.isNotEmpty()) {
                LoadResult.Page(
                    data = searchResult,
                    prevKey = if (currentPage == 1) null else currentPage - 1,
                    nextKey = if (endOfPageReached) null else currentPage + 1
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

    override fun getRefreshKey(state: PagingState<Int, SmartMovement>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(INDEX_ONE)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(INDEX_ONE)
        }
    }

    companion object {
        const val INDEX_ONE = 1
        const val PAGE_SIZE = 10
    }
}
