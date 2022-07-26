package com.multimoney.data.mapper.security

import com.multimoney.data.networking.security.apollomodel.UserValidationMutation
import com.multimoney.domain.model.security.UserData

private fun UserValidationMutation.UserValidation.mapToDomainModel() = UserData(
    pkUser = pkUser ?: "",
    userName = userName ?: "",
    email = email ?: "",
    phoneNumber = phoneNumber,
    fullName = fullName,
    firstName = firstName,
    secondName = secondName,
    firstLastName = lastName,
    secondLastName = secondLastName,
    nationality = nationality,
    identification = identification,
    countryCode = countryCode,
    currentStep = currentStep ?: "",
    userStatus = userStatus
)

fun UserValidationMutation.Data.mapToDomainModel() = userValidation?.mapToDomainModel()