package com.multimoney.data.repository

import com.multimoney.data.base.BaseRepository
import com.multimoney.data.mapper.balances.mapToDomainModel
import com.multimoney.data.networking.BalanceApi
import com.multimoney.domain.model.balance.Balance
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.util.MultimoneyResult.Success
import com.multimoney.domain.repository.BalanceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BalanceRepositoryImpl @Inject constructor(
    private val balanceApi: BalanceApi
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
        apolloCall = balanceApi.queryBalance(
            user, identification, idBrand, idClient, idLoanClient,
            creditStatus,
            accountStatus,
            cryptoStatus,
            cardStatus
        ),
        apolloCallMapper = { data ->
            Success(data.mapToDomainModel())
        }
    )
}
