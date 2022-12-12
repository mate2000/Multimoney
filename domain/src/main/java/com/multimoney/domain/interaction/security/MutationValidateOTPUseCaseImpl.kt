package com.multimoney.domain.interaction.security

import com.multimoney.domain.repository.SecurityRepository


class MutationValidateOTPUseCaseImpl(private val securityRepository: SecurityRepository) :
    MutationValidateOTPUseCase {
    override suspend fun invoke(
        email: String,
        otp : String
    ) = securityRepository.mutationValidateOTP(
        email,
        otp
    )
}
