package com.multimoney.domain.interaction.virtualcard

import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.virtualcard.AutomaticCardDebit
import com.multimoney.domain.repository.VirtualCardRepository
import kotlinx.coroutines.flow.Flow

class MutationActivatedCardAutomaticDebitUseCaseImpl(private val virtualCardRepository: VirtualCardRepository) :
    MutationActivatedCardAutomaticDebitUseCase {

    override suspend fun invoke(
        user: String,
        idBrand: Int,
        idClient: Int,
        idLoanClient: Int,
        idCard: Long,
        cardMasked: String
    ): Flow<MultimoneyResult<AutomaticCardDebit?>> =
        virtualCardRepository.mutationActivatedCardAutomaticDebit(
            user,
            idBrand,
            idClient,
            idLoanClient,
            idCard,
            cardMasked
        )
}
