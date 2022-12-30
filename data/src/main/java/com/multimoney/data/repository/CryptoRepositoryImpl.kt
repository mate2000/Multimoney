package com.multimoney.data.repository

import com.multimoney.data.base.BaseRepository
import com.multimoney.data.mapper.crypto.mapToDomainModel
import com.multimoney.data.networking.GraphqlApi
import com.multimoney.domain.model.crypto.CryptoCurrencyMovement
import com.multimoney.domain.model.crypto.GetHistoricalClientBalance
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.util.MultimoneyResult.Success
import com.multimoney.domain.repository.CryptoRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class CryptoRepositoryImpl @Inject constructor(
    private val graphqlApi: GraphqlApi
) : BaseRepository(), CryptoRepository {

    override suspend fun getHistoricalClientBalance(
        user: String,
        idBrand: Int,
        identification: String,
        baseAsset: String,
        startDate: String,
        endDate: String
    ): Flow<MultimoneyResult<GetHistoricalClientBalance?>> = fetchData(
        apolloCall = graphqlApi.queryGetHistoricalClientBalance(
            user,
            idBrand,
            identification,
            baseAsset,
            startDate,
            endDate
        ),
        apolloCallMapper = { data -> Success(data.mapToDomainModel()) }
    )

    override suspend fun getCryptoCurrencyMovements(
        user: String,
        idBrand: Int,
        identification: String,
        market: String,
        startDate: String,
        endDate: String
    ): Flow<MultimoneyResult<CryptoCurrencyMovement?>> = fetchData(
        apolloCall = graphqlApi.queryCryptoCurrencyMovements(
            user,
            idBrand,
            identification,
            market,
            startDate,
            endDate
        ),
        apolloCallMapper = { data -> Success(data.cryptoCurrencyMovement.mapToDomainModel()) }
    )

    override suspend fun getCryptoCurrencyHistory(
        market: String,
        user: String,
        idBrand: Int,
        startDate: String,
        endDate: String,
        maxPoints: Long,
        paginationLimit: Int,
        paginationOffset: Int
    ): Flow<MultimoneyResult<GetHistoricalClientBalance?>> = fetchData(
        apolloCall = graphqlApi.queryCryptoPriceHistory(
            market,
            user,
            idBrand,
            startDate,
            endDate,
            maxPoints,
            paginationLimit,
            paginationOffset
        ),
        apolloCallMapper = { data -> Success(data.mapToDomainModel()) }
    )

}