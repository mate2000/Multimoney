package com.multimoney.domain.interaction.virtualcard

import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.virtualcard.UpdateCard
import com.multimoney.domain.repository.VirtualCardRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MutationUpdateCardVDUseCaseImpl @Inject constructor(private val virtualCardRepository: VirtualCardRepository) :
    MutationUpdateCardVDUseCase {
    override suspend fun invoke(
        idCard: Long,
        identification: String,
        cardDescription: String,
        cardMasked: String,
        expirationMonth: String,
        expirationYear: String,
        verificationValue: String,
        default: Boolean,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<UpdateCard?>> =
        virtualCardRepository.mutationUpdateCardVD(
            idCard = idCard,
            identification = identification,
            cardDescription = cardDescription,
            cardMasked = cardMasked,
            expirationMonth = expirationMonth,
            expirationYear = expirationYear,
            verificationValue = verificationValue,
            default = default,
            user = user,
            idBrand = idBrand
        )
}