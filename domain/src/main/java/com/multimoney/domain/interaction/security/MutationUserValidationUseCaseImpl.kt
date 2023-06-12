package com.multimoney.domain.interaction.security

import com.multimoney.domain.repository.SecurityRepository

class MutationUserValidationUseCaseImpl(private val securityRepository: SecurityRepository) :
    MutationUserValidationUseCase {
    override suspend fun invoke(
        email: String,
        currentStep: String,
        idBrand: Int,
        idDocument: Int,
        identification: String,
        firstName: String,
        secondName: String,
        firstSurname: String,
        secondSurname: String,
        deviceId: String
    ) = securityRepository.mutationUserValidation(
        email,
        currentStep,
        idBrand,
        idDocument,
        identification,
        firstName,
        secondName,
        firstSurname,
        secondSurname,
        deviceId
    )
}
