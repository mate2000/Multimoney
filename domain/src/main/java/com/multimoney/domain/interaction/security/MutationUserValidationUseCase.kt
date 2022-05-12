package com.multimoney.domain.interaction.security

import com.multimoney.domain.model.security.User
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface MutationUserValidationUseCase {
    suspend operator fun invoke(
        email: String,
        currentStep: String,
        idBrand: Int
    ): Flow<MultimoneyResult<User?>>
}