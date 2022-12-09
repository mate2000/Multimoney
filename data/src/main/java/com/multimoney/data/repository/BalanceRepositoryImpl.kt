package com.multimoney.data.repository

import com.multimoney.data.base.BaseRepository
import com.multimoney.data.mapper.balances.mapToDomainModel
import com.multimoney.data.networking.GraphqlApi
import com.multimoney.domain.model.balance.Balance
import com.multimoney.domain.model.balance.BalanceCardInformation
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.util.MultimoneyResult.Success
import com.multimoney.domain.repository.BalanceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BalanceRepositoryImpl @Inject constructor(
    private val graphqlApi: GraphqlApi
) : BaseRepository(),
    BalanceRepository {

    override suspend fun queryBalance(
        user: String,
        identification: String,
        idBrand: Int,
        idClient: Int,
        idLoanClient: Int,
        creditStatus: Int,
        accountStatus: Int,
        cryptoStatus: Int,
        cardStatus: Int
    ): Flow<MultimoneyResult<Balance?>> = fetchData(
        apolloCall = graphqlApi.queryBalance(
            user,
            identification,
            idBrand,
            idClient,
            idLoanClient,
            creditStatus,
            accountStatus,
            cryptoStatus,
            cardStatus
        ),
        apolloCallMapper = { data ->
            Success(data.mapToDomainModel())
        }
    )

    override suspend fun queryBalanceCardInformation(
        user: String,
        identification: String,
        idBrand: Int,
        idClient: Int,
        idLoanClient: Int,
        cardStatus: Int
    ): Flow<MultimoneyResult<BalanceCardInformation?>> = fetchData(
        apolloCall = graphqlApi.queryBalanceCardInformation(
            user,
            identification,
            idBrand,
            idClient,
            idLoanClient,
            cardStatus
        ),
        apolloCallMapper = { data ->
            Success(data.mapToDomainModel())
        }
    )
}
