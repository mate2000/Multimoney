package com.multimoney.domain.interaction.credit;

import com.multimoney.domain.model.credit.SaveCreditOffer
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.CreditRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MutationSaveCreditOfferUseCaseImpl @Inject constructor(val creditRepository: CreditRepository) :
    MutationSaveCreditOfferUseCase {
    override suspend fun invoke(
        pkUser: Long,
        idUserRequest: Long,
        idBrand: Int
    ): Flow<MultimoneyResult<SaveCreditOffer?>> =
        creditRepository.mutationSaveCreditOffer(
            pkUser = pkUser,
            idUserRequest = idUserRequest,
            idBrand = idBrand
        )
}
