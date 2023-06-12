package com.multimoney.domain.interaction.security

import com.multimoney.domain.model.security.UserPhoneMobileSave
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SecurityRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class MutationUserPhoneMobileSaveUseCaseImpl @Inject constructor(val repository: SecurityRepository) :
    MutationUserPhoneMobileSaveUseCase {
    override suspend fun invoke(
        idBrand: Int,
        idWalletCard: String,
        manufacture: String,
        pkUser: Long,
        serialNumber: String,
        user: String
    ): Flow<MultimoneyResult<UserPhoneMobileSave?>> = repository.mutationUserPhoneMobileSave(
        idBrand,
        idWalletCard,
        manufacture,
        pkUser,
        serialNumber,
        user
    )
}
