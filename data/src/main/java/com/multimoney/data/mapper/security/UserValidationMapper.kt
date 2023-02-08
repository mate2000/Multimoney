package com.multimoney.data.mapper.security

import com.multimoney.data.networking.graphql.apollomodel.UserValidationMutation
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
    userStatus = userStatus,
    isNewUser = isNewUser,
    maskedMail = maskedMail,
    maskedPhoneNumber = maskedPhoneNumber,
    message = message,
    status = status,
    detail = detail
)

fun UserValidationMutation.Data.mapToDomainModel() = userValidation.mapToDomainModel()
