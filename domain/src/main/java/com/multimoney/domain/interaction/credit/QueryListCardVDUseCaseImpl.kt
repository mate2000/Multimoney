package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.CardVisaDirect
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.CreditRepository
import kotlinx.coroutines.flow.Flow

class QueryListCardVDUseCaseImpl(val creditRepository: CreditRepository) : QueryListCardVDUseCase {

    override suspend fun invoke(
        user: String,
        idBrand: Int,
        identification: String
    ): Flow<MultimoneyResult<List<CardVisaDirect?>?>> =
        creditRepository.queryListCardVD(user, idBrand, identification)
}
