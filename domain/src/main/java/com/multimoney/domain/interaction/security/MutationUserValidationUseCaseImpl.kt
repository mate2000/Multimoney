package com.multimoney.domain.interaction.security

import com.multimoney.domain.repository.SecurityRepository
import javax.inject.Inject

class MutationUserValidationUseCaseImpl @Inject constructor(private val securityRepository: SecurityRepository) :
    MutationUserValidationUseCase {
    override suspend fun invoke(
        email: String,
        currentStep: String,
        idBrand: Int
    ) = securityRepository.mutationUserValidation(email, currentStep, idBrand)
}
