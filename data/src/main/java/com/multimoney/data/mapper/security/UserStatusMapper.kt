package com.multimoney.data.mapper.security

import com.multimoney.data.networking.security.apollomodel.ValidateUserStatusQuery
import com.multimoney.domain.model.security.InfoBankAccount
import com.multimoney.domain.model.security.InfoCredit
import com.multimoney.domain.model.security.InfoCrypto
import com.multimoney.domain.model.security.InfoUser
import com.multimoney.domain.model.security.InfoVirtualCard
import com.multimoney.domain.model.security.ValidateUserStatus

fun ValidateUserStatusQuery.ValidateUserStatus.mapToDomainModel() = ValidateUserStatus(
    infoUser = infoUser?.mapToDomainModel(),
    infoCredit = infoCredit?.mapToDomainModel(),
    infoCrypto = infoCrypto?.mapToDomainModel(),
    infoVirtualCard = infoVirtualCard?.mapToDomainModel(),
    infoBankAccount = infoBankAccount?.mapToDomainModel()
)

fun ValidateUserStatusQuery.InfoUser.mapToDomainModel() = InfoUser(
    pkUser = pkUser.toString().toInt(),
    idBrand = idBrand.toString().toInt(),
    fullName = fullName,
    firstName = firstName,
    secondName = secondName,
    lastName = lastName,
    secondLastName = secondLastName,
    userName = userName,
    email = email,
    phone = phone,
    birthDate = birthDate.toString(),
    idClient = idClient.toString().toInt(),
    vISADirect_ID = vISADirect_ID,
    vISADirect_Usuario = vISADirect_Usuario,
    fecha_ultimo_acceso = fecha_ultimo_acceso.toString()
)

fun ValidateUserStatusQuery.InfoCrypto.mapToDomainModel() = InfoCrypto(
    status = status
)

fun ValidateUserStatusQuery.InfoVirtualCard.mapToDomainModel() = InfoVirtualCard(
    status = status
)

fun ValidateUserStatusQuery.InfoCredit.mapToDomainModel() = InfoCredit(
    idClient = idClient.toString().toInt(),
    idLoanClient = idLoanClient.toString().toInt(),
    status = status,
    amountAvailable = amountAvailable.toString().toFloat()
)

fun ValidateUserStatusQuery.InfoBankAccount.mapToDomainModel() = InfoBankAccount(
    statusFirm = statusFirm,
    statusOnfido = statusOnfido,
    idRequestSysde = idRequestSysde.toString().toInt(),
    idRequestGlobal = idRequestGlobal.toString().toInt(),
    status = status
)

fun ValidateUserStatusQuery.Data.mapToDomainModel() = validateUserStatus?.mapToDomainModel()