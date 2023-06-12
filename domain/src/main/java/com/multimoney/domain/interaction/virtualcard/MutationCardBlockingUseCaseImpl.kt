package com.multimoney.domain.interaction.virtualcard

import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.virtualcard.CardBlocking
import com.multimoney.domain.repository.VirtualCardRepository
import kotlinx.coroutines.flow.Flow

class MutationCardBlockingUseCaseImpl(private val virtualCardRepository: VirtualCardRepository) :
    MutationCardBlockingUseCase {

    override suspend fun invoke(
        blockType: String,
        observations: String,
        idClient: Int,
        userApp: String,
        cardToken: String,
        source: String,
        idLoan: Int,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<CardBlocking?>> =
        virtualCardRepository.mutationCardBlocking(
            blockType,
            observations,
            idClient,
            userApp,
            cardToken,
            source,
            idLoan,
            user,
            idBrand
        )
}
