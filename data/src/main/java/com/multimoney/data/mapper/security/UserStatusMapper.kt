package com.multimoney.data.mapper.security

import com.multimoney.data.networking.security.apollomodel.ValidateUserStatusQuery
import com.multimoney.domain.model.security.InfoBankAccount
import com.multimoney.domain.model.security.InfoCredit
import com.multimoney.domain.model.security.InfoPreApprove
import com.multimoney.domain.model.security.InfoUser
import com.multimoney.domain.model.security.Product
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
    amountAvailable = 0.0,
    statusFirm = "",
    infoPreApprove = infoPreApprove?.mapToDomainModel()
)

private fun ValidateUserStatusQuery.InfoPreApprove.mapToDomainModel() = InfoPreApprove(
    idUserRequest = idUserRequest,
    status = status,
    selectedAmount = selectedAmount?.toFloat(),
    statusFirm = statusFirm,
    currentStep = currentStep,
    infoProducts = infoProducts?.map { it?.mapToDomainModel() }
)

private fun ValidateUserStatusQuery.InfoProduct.mapToDomainModel() =
    Product(idProduct, amountAvailable, symbolCurrency)

private fun ValidateUserStatusQuery.InfoBankAccount.mapToDomainModel() = InfoBankAccount(
    statusFirm = statusFirm
)

fun ValidateUserStatusQuery.Data.mapToDomainModel() = validateUserStatus?.mapToDomainModel()