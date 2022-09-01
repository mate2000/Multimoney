package com.multimoney.domain.interaction.security

import com.multimoney.domain.model.security.UserData
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QueryValidateUserExistsUseCase {
    suspend operator fun invoke(
        email: String,
        currentStep: String
    ): Flow<MultimoneyResult<UserData?>>
}