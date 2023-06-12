package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.SmartAccountStatusResult
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SmartAccountRepository
import kotlinx.coroutines.flow.Flow

class MutationAccountStatusUseCaseImpl(val repository: SmartAccountRepository) :
    MutationAccountStatusUseCase {
    override suspend fun invoke(
        user: String,
        idBrand: Int,
        identificationNumber: String,
        newState: String,
        typeState: String,
        idAccountSysde: Long,
        idAccountRequest: Long
    ): Flow<MultimoneyResult<SmartAccountStatusResult?>> =
        repository.mutationUpdateSmartAccountStatus(
            user,
            idBrand,
            identificationNumber,
            newState,
            typeState,
            idAccountSysde,
            idAccountRequest
        )
}