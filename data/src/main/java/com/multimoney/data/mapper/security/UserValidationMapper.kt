package com.multimoney.data.mapper.security

import com.multimoney.data.networking.security.apollomodel.UserValidationMutation
import com.multimoney.domain.model.security.UserData

fun UserValidationMutation.UserValidation.mapToDomainModel() = UserData(
    pkUser = pkUser ?: "",
    userName = userName ?: "",
    email = email ?: "",
    phoneNumber = phoneNumber,
    fullName = fullName,
    firstName = firstName,
    secondName = secondName,
    lastName = lastName,
    secondLastName = secondLastName,
    contactMeans = contactMeans,
    nationality = nationality,
    identification = identification,
    countryCode = countryCode,
    currentStep = currentStep ?: "",
    userStatus = userStatus
)