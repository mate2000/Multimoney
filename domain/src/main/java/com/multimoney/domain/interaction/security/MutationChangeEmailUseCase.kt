package com.multimoney.domain.interaction.security

import com.multimoney.domain.model.security.ChangeEmail
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface MutationChangeEmailUseCase {
    suspend operator fun invoke(
        pkUser: Int,
        identification: String,
        email: String,
        registerId: Int,
        changeUser: Boolean,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<ChangeEmail>>
}