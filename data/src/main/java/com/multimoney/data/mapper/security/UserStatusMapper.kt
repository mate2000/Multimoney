package com.multimoney.data.mapper.security

import com.multimoney.data.networking.security.apollomodel.ValidateUserStatusQuery
import com.multimoney.domain.model.security.InfoBankAccount
import com.multimoney.domain.model.security.InfoCredit
import com.multimoney.domain.model.security.InfoUser
import com.multimoney.domain.model.security.ValidateUserStatus

private fun ValidateUserStatusQuery.ValidateUserStatus.mapToDomainModel() = ValidateUserStatus(
    infoUser = infoUser?.mapToDomainModel(),
    infoCredit = infoCredit?.mapToDomainModel(),
    infoBankAccount = infoBankAccount?.mapToDomainModel()
)

private fun ValidateUserStatusQuery.InfoUser.mapToDomainModel() = InfoUser(
    idBrand = idBrand.toString().toInt(),
    userName = userName,
    idClient = idClient.toString().toInt(),
    statusOnfido = statusOnfido
)

private fun ValidateUserStatusQuery.InfoCredit.mapToDomainModel() = InfoCredit(
    idClient = idClient.toString().toInt(),
    idLoanClient = idLoanClient.toString().toInt(),
    status = status,
    amountAvailable = amountAvailable.toString().toDouble(),
    idRequest = idRequest
)

private fun ValidateUserStatusQuery.InfoBankAccount.mapToDomainModel() = InfoBankAccount(
    statusFirm = statusFirm
)

fun ValidateUserStatusQuery.Data.mapToDomainModel() = validateUserStatus?.mapToDomainModel()