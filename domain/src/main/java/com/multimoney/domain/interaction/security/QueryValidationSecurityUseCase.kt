package com.multimoney.domain.interaction.security

import com.multimoney.domain.model.security.ValidateSecurity
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QueryValidationSecurityUseCase {
    suspend operator fun invoke(
        pkUser: String,
        password: String,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<ValidateSecurity?>>
}