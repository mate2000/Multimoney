package com.multimoney.data.mapper.security

import com.multimoney.data.networking.graphql.apollomodel.UpdateUserRegisterMutation
import com.multimoney.domain.model.security.UserData

private fun UpdateUserRegisterMutation.UpdateUserRegister.mapToDomainModel() = UserData(
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

fun UpdateUserRegisterMutation.Data.mapToDomainModel() = updateUserRegister.mapToDomainModel()
