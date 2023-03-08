package com.multimoney.domain.interaction.virtualcard

import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.virtualcard.CreateCard
import com.multimoney.domain.repository.VirtualCardRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MutationCreateCardVDUseCaseImpl @Inject constructor(private val virtualCardRepository: VirtualCardRepository) :
    MutationCreateCardVDUseCase {
    override suspend fun invoke(
        identification: String,
        cardTokenID: String,
        default: Boolean,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<CreateCard?>> =
        virtualCardRepository.mutationCreateCardVD(
            identification = identification,
            cardTokenID = cardTokenID,
            default = default,
            user = user,
            idBrand = idBrand
        )
}