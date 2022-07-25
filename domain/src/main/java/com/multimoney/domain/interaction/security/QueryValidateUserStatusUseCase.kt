package com.multimoney.domain.interaction.security

import com.multimoney.domain.model.security.ValidateUserStatus
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QueryValidateUserStatusUseCase {
    suspend operator fun invoke(
        pkUser: Int,
        identification: String,
        email: String,
        idBrand: Int
    ): Flow<MultimoneyResult<ValidateUserStatus?>>
}