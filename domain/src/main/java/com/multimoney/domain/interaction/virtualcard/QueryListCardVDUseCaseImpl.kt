package com.multimoney.domain.interaction.virtualcard

import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.virtualcard.CardVisaDirect
import com.multimoney.domain.repository.VirtualCardRepository
import kotlinx.coroutines.flow.Flow

class QueryListCardVDUseCaseImpl(private val virtualCardRepository: VirtualCardRepository) : QueryListCardVDUseCase {

    override suspend fun invoke(
        user: String,
        idBrand: Int,
        identification: String
    ): Flow<MultimoneyResult<List<CardVisaDirect?>?>> =
        virtualCardRepository.queryListCardVD(user, idBrand, identification)
}
