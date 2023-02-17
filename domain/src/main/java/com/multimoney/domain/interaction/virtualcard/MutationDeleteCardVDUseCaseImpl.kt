package com.multimoney.domain.interaction.virtualcard

import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.virtualcard.DeleteCard
import com.multimoney.domain.repository.VirtualCardRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MutationDeleteCardVDUseCaseImpl @Inject constructor(private val virtualCardRepository: VirtualCardRepository) :
    MutationDeleteCardVDUseCase {
    override suspend fun invoke(
        identification: String,
        user: String,
        idBrand: Int,
        idCard: Long
    ): Flow<MultimoneyResult<DeleteCard?>> =
        virtualCardRepository.mutationDeleteCardVD(
            identification = identification,
            user = user,
            idBrand = idBrand,
            idCard = idCard
        )
}