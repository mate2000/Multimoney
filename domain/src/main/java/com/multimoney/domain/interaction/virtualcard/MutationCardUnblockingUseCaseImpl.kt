package com.multimoney.domain.interaction.virtualcard

import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.virtualcard.CardUnblocking
import com.multimoney.domain.repository.VirtualCardRepository
import kotlinx.coroutines.flow.Flow

class MutationCardUnblockingUseCaseImpl(private val virtualCardRepository: VirtualCardRepository) :
    MutationCardUnblockingUseCase {

    override suspend fun invoke(
        observations: String,
        idClient: Int,
        userApp: String,
        cardToken: String,
        source: String,
        idLoan: Int,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<CardUnblocking?>> =
        virtualCardRepository.mutationCardUnblocking(
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
