package com.multimoney.domain.interaction.security

import com.multimoney.domain.repository.SecurityRepository

class MutationUserValidationUseCaseImpl(private val securityRepository: SecurityRepository) :
    MutationUserValidationUseCase {
    override suspend fun invoke(
        email: String,
        currentStep: String,
        idBrand: Int
    ) = securityRepository.mutationUserValidation(email, currentStep, idBrand)
}
