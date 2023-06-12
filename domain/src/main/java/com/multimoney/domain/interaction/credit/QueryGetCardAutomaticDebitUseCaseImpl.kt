package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.virtualcard.CardVisaDirect
import com.multimoney.domain.repository.CreditRepository
import kotlinx.coroutines.flow.Flow

class QueryGetCardAutomaticDebitUseCaseImpl(val creditRepository: CreditRepository) :
    QueryGetCardAutomaticDebitUseCase {
    override suspend fun invoke(
        user: String,
        identification: String,
        idBrand: Int,
        idClient: Long,
        idLoanClient: Long
    ): Flow<MultimoneyResult<List<CardVisaDirect?>?>> =
        creditRepository.queryGetCardAutomaticDebit(user, identification, idBrand, idClient, idLoanClient)
}
