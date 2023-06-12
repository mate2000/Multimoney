package com.multimoney.data.mapper.security

import com.multimoney.data.networking.graphql.apollomodel.ValidateUserExistsQuery
import com.multimoney.domain.model.security.UserData

private fun ValidateUserExistsQuery.ValidateUserExists.mapToDomainModel() = UserData(
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
    strIdIdentification = strIdIdentification,
    idIdentification = idIdentification,
    countryCode = countryCode,
    currentStep = currentStep ?: "",
    userStatus = userStatus,
    isNewUser = isNewUser,
    maskedMail = maskedMail,
    maskedPhoneNumber = maskedPhoneNumber,
    message = message,
    status = status,
    detail = detail,
    idBrand = idBrand
)

fun ValidateUserExistsQuery.Data.mapToDomainModel() = validateUserExists.mapToDomainModel()
