package com.multimoney.domain.interaction.security

import com.multimoney.domain.repository.SecurityRepository

class MutationUpdateUserRegisterUseCaseImpl(private val securityRepository: SecurityRepository) :
    MutationUpdateUserRegisterUseCase {
    override suspend fun invoke(
        pkUser: String,
        user: String,
        email: String,
        phoneNumber: String?,
        fullName: String?,
        firstName: String?,
        secondName: String?,
        lastName: String?,
        secondLastName: String?,
        nationality: String?,
        identification: String?,
        countryCode: String?,
        currentStep: String,
        idBrand: Int
    ) = securityRepository.mutationUpdateUserRegister(
        pkUser,
        user,
        email,
        phoneNumber,
        fullName,
        firstName,
        secondName,
        lastName,
        secondLastName,
        nationality,
        identification,
        countryCode,
        currentStep,
        idBrand
    )
}
