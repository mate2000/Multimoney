package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.SaveSmartAccount
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SmartAccountRepository
import kotlinx.coroutines.flow.Flow

class MutationSaveAutomatedSmartAccountUseCaseImpl(val smartAccountRepository: SmartAccountRepository) :
    MutationSaveAutomatedSmartAccountUseCase {
    override suspend operator fun invoke(
        user: String,
        idBrand: Int,
        identificationNumber: String,
        idRequest: Long
    ): Flow<MultimoneyResult<SaveSmartAccount?>> =
        smartAccountRepository.mutationSaveAutomatedSmartAccount(
            user,
            idBrand,
            identificationNumber,
            idRequest
        )
}
