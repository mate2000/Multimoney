package com.multimoney.data.repository

import com.multimoney.data.base.BaseRepository
import com.multimoney.data.mapper.crypto.mapToDomainModel
import com.multimoney.data.networking.GraphqlApi
import com.multimoney.domain.model.crypto.GetHistoricalClientBalance
import com.multimoney.domain.model.crypto.GetListOfAvailableCryptoCoins
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.util.MultimoneyResult.Success
import com.multimoney.domain.repository.CryptoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

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

    override suspend fun getAvailableListOfCryptoCoins(
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<GetListOfAvailableCryptoCoins?>> = fetchData(
        apolloCall = graphqlApi.queryGetAvailableListOfCryptoCoins(user, idBrand),
        apolloCallMapper = { data -> Success(data.mapToDomainModel()) }
    )

}