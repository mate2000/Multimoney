package com.multimoney.domain.interaction.balance

import com.multimoney.domain.repository.BalanceRepository

class QueryBalanceUseCaseImpl(val balanceRepository: BalanceRepository) :
    QueryBalanceUseCase {
    override suspend fun invoke(
        user: String,
        identification: String,
        idBrand: Int,
        idClient: String,
        idLoanClient: Int
    ) =
        balanceRepository.queryBalance(
            user,
            identification,
            idBrand,
            idClient,
            idLoanClient
        )
}