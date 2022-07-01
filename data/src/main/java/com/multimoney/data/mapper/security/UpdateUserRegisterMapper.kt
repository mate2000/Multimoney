package com.multimoney.data.mapper.security

import com.multimoney.data.networking.security.apollomodel.UpdateUserRegisterMutation
import com.multimoney.domain.model.security.UserData

fun UpdateUserRegisterMutation.UpdateUserRegister.mapToDomainModel() = UserData(
    pkUser = pkUser ?: "",
    userName = userName ?: "",
    email = email ?: "",
    phoneNumber = phoneNumber,
    fullName = fullName,
    firstName = firstName,
    secondName = secondName,
    lastName = lastName,
    secondLastName = secondLastName,
    nationality = nationality,
    identification = identification,
    countryCode = countryCode,
    currentStep = currentStep ?: "",
    userStatus = userStatus
)

fun UpdateUserRegisterMutation.Data.mapToDomainModel() = updateUserRegister?.mapToDomainModel()