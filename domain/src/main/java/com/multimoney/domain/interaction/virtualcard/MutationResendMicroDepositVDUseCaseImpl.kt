package com.multimoney.domain.interaction.virtualcard

import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.virtualcard.ResendMicroDepositVD
import com.multimoney.domain.repository.VirtualCardRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MutationResendMicroDepositVDUseCaseImpl @Inject constructor(private val virtualCardRepository: VirtualCardRepository) :
    MutationResendMicroDepositVDUseCase {
    override suspend fun invoke(
        identification: String,
        idCard: String,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<ResendMicroDepositVD?>> =
        virtualCardRepository.mutationResendMicroDepositVD(
            identification = identification,
            idCard = idCard,
            user = user,
            idBrand = idBrand
        )
}
