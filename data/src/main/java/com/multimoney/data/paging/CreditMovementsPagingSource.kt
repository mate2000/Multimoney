package com.multimoney.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.multimoney.data.mapper.credit.mapToDomainModel
import com.multimoney.data.networking.GraphqlApi
import com.multimoney.data.util.fetchData
import com.multimoney.domain.model.credit.CreditMovement
import com.multimoney.domain.model.util.MultimoneyResult.Success
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onSuccess
import kotlinx.coroutines.flow.collectLatest

class CreditMovementsPagingSource(
    private val graphqlApi: GraphqlApi,
    private val idBrand: Int,
    private val idLoanClient: Int,
    private val option: String,
    private val pageSize: Int
) : PagingSource<Int, CreditMovement>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CreditMovement> {
        val currentPage = params.key ?: INDEX_ONE
        return try {
            val response = fetchData(
                apolloCall = graphqlApi.queryGetPromissoryNoteDetail(
                    idBrand,
                    idLoanClient,
                    currentPage,
                    pageSize,
                    option
                ),
                apolloCallMapper = { data ->
                    Success(data.mapToDomainModel())
                }
            )

            var searchResult = emptyList<CreditMovement>()
            val movesResult = mutableListOf<CreditMovement>()
            var endOfPageReached = false

            response.collectLatest { result ->
                result.onSuccess { promissoryNoteDetail ->
                    promissoryNoteDetail.movementsResultList?.let { movementResultList ->
                        movementResultList.forEach { creditMovementsResult ->
                            creditMovementsResult.result?.let { movesResult.addAll(it) }
                        }
                    }
                    searchResult = movesResult
                    endOfPageReached = pageSize * currentPage > (promissoryNoteDetail.totalRecords ?: 0)
                }
                result.onFailure { error ->
                    error.throwable?.let { LoadResult.Error<Int, CreditMovement>(it) }
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

    override fun getRefreshKey(state: PagingState<Int, CreditMovement>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(INDEX_ONE)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(INDEX_ONE)
        }
    }

    companion object {
        const val INDEX_ONE = 1
    }
}
