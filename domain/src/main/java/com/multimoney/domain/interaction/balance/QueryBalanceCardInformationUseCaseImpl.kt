package com.multimoney.domain.interaction.balance

import com.multimoney.domain.model.balance.BalanceCardInformation
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.BalanceRepository
import kotlinx.coroutines.flow.Flow

class QueryBalanceCardInformationUseCaseImpl(val repository: BalanceRepository) : QueryBalanceCardInformationUseCase {
    override suspend fun invoke(
        user: String,
        identification: String,
        idBrand: Int,
        idClient: Int,
        idLoanClient: Int,
        cardStatus: Int
    ): Flow<MultimoneyResult<BalanceCardInformation?>> = repository.queryBalanceCardInformation(
        user,
        identification,
        idBrand,
        idClient,
        idLoanClient,
        cardStatus
    )
}
