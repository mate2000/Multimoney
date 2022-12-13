package com.multimoney.data.repository

import com.multimoney.data.base.BaseRepository
import com.multimoney.data.mapper.mmvisa.mapToDomainModel
import com.multimoney.data.networking.GraphqlApi
import com.multimoney.domain.model.mmvisa.CardIssuanceNV
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.util.MultimoneyResult.Success
import com.multimoney.domain.repository.MultimoneyVisaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MultimoneyVisaRepositoryImpl @Inject constructor(
    private val graphqlApi: GraphqlApi
) : BaseRepository(), MultimoneyVisaRepository {
    override suspend fun queryCardIssuanceNV(
        idClient: Long,
        requestType: String,
        identification: String,
        idLoanClient: Int,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<CardIssuanceNV?>> = fetchData(
        apolloCall = graphqlApi.queryCardIssuanceNV(
            idClient,
            requestType,
            identification,
            idLoanClient,
            user,
            idBrand
        ),
        apolloCallMapper = { data ->
            Success(data.mapToDomainModel())
        }
    )
}
