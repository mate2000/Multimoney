package com.multimoney.domain.interaction.accountsmart


import com.multimoney.domain.model.accountsmart.SaveSinpeAccount
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SmartAccountRepository
import kotlinx.coroutines.flow.Flow

class MutationSinpeAccountDeleteUseCaseImpl(val repository: SmartAccountRepository) :
    MutationSinpeAccountDeleteUseCase {
    override suspend fun invoke(
        user: String,
        idBrand: Int,
        identification: String,
        idAccount: Int?,
    ): Flow<MultimoneyResult<SaveSinpeAccount?>> = repository.mutationManageSinpeAccountDelete(
        user = user,
        idBrand = idBrand,
        identification = identification,
        idAccount = idAccount,
    )
}