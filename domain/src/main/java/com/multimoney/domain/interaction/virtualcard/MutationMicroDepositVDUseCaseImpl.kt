package com.multimoney.domain.interaction.virtualcard

import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.virtualcard.MicroDepositVD
import com.multimoney.domain.repository.VirtualCardRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MutationMicroDepositVDUseCaseImpl @Inject constructor(private val virtualCardRepository: VirtualCardRepository) :
    MutationMicroDepositVDUseCase {
    override suspend fun invoke(
        identification: String,
        idCard: String,
        code: String,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<MicroDepositVD?>> =
        virtualCardRepository.mutationMicroDepositVD(
            identification = identification,
            idCard = idCard,
            code = code,
            user = user,
            idBrand = idBrand
        )
}
