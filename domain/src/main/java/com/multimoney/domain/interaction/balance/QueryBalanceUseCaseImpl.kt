package com.multimoney.domain.interaction.balance

import com.multimoney.domain.repository.BalanceRepository

class QueryBalanceUseCaseImpl(private val balanceRepository: BalanceRepository) :
    QueryBalanceUseCase {
    override suspend fun invoke(
        user: String,
        identification: String,
        idBrand: Int,
        idClient: Int,
        idLoanClient: Int,
        creditStatus: Int,
        accountStatus: Int,
        cryptoStatus: Int,
        cardStatus: Int
    ) =
        balanceRepository.queryBalance(
            user,
            identification,
            idBrand,
            idClient,
            idLoanClient,
            creditStatus,
            accountStatus,
            cryptoStatus,
            cardStatus
        )
}