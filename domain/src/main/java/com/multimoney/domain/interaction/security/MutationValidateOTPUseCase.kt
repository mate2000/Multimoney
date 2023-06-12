package com.multimoney.domain.interaction.security

import com.multimoney.domain.model.security.ValidateOTP
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface MutationValidateOTPUseCase {
    suspend operator fun invoke(
        email: String,
        otp : String
    ): Flow<MultimoneyResult<ValidateOTP?>>
}