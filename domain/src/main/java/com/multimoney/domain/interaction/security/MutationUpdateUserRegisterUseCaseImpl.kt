package com.multimoney.domain.interaction.security

import com.multimoney.domain.repository.SecurityRepository
import javax.inject.Inject

class MutationUpdateUserRegisterUseCaseImpl @Inject constructor(private val securityRepository: SecurityRepository) :
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
        contactMeans: String?,
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
        contactMeans,
        nationality,
        identification,
        countryCode,
        currentStep,
        idBrand
    )
}
