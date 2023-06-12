package com.multimoney.domain.interaction.security

import com.multimoney.domain.model.security.UserPhoneMobileSave
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface MutationUserPhoneMobileSaveUseCase {
    suspend operator fun invoke(
        idBrand: Int,
        idWalletCard: String,
        manufacture: String,
        pkUser: Long,
        serialNumber: String,
        user: String
    ): Flow<MultimoneyResult<UserPhoneMobileSave?>>
}
