package com.multimoney.data.mapper.security

import com.multimoney.data.networking.graphql.apollomodel.ValidateUserStatusQuery
import com.multimoney.domain.model.security.InfoBankAccount
import com.multimoney.domain.model.security.InfoCredit
import com.multimoney.domain.model.security.InfoCrypto
import com.multimoney.domain.model.security.InfoPreApprove
import com.multimoney.domain.model.security.InfoUser
import com.multimoney.domain.model.security.InfoVirtualCard
import com.multimoney.domain.model.security.Product
import com.multimoney.domain.model.security.ValidateUserStatus

private fun ValidateUserStatusQuery.ValidateUserStatus.mapToDomainModel() = ValidateUserStatus(
    infoUser = infoUser.mapToDomainModel(),
    infoCredit = infoCredit.mapToDomainModel(),
    infoBankAccount = infoBankAccount.mapToDomainModel(),
    infoCrypto = infoCrypto.mapToDomainModel(),
    infoVirtualCard = infoVirtualCard.mapToDomainModel()
)

private fun ValidateUserStatusQuery.InfoUser.mapToDomainModel() = InfoUser(
    idBrand = idBrand.toString().toInt(),
    userName = userName,
    idClient = idClient.toString().toInt(),
    firstName = firstName,
    lastName = lastName,
    secondLastName = secondLastName,
    statusOnfido = statusOnfido
)

private fun ValidateUserStatusQuery.InfoCredit.mapToDomainModel() = InfoCredit(
    idClient = idClient.toString().toInt(),
    idLoanClient = idLoanClient.toString().toInt(),
    status = status,
    infoPreApprove = infoPreApprove?.mapToDomainModel()
)

private fun ValidateUserStatusQuery.InfoPreApprove.mapToDomainModel() = InfoPreApprove(
    idUserRequest = idUserRequest.toString().toInt(),
    status = status,
    selectedAmount = selectedAmount.toString().toFloat(),
    statusFirm = statusFirm,
    currentStep = currentStep,
    infoProducts = infoProducts.map { it.mapToDomainModel() }
)

private fun ValidateUserStatusQuery.InfoProduct.mapToDomainModel() =
    Product(idProduct, amountAvailable.toString(), amountAvailableFormat, symbolCurrency)

private fun ValidateUserStatusQuery.InfoBankAccount.mapToDomainModel() = InfoBankAccount(
    statusFirm = statusFirm,
    status = status
)

private fun ValidateUserStatusQuery.InfoCrypto.mapToDomainModel() = InfoCrypto(status = status)

private fun ValidateUserStatusQuery.InfoVirtualCard.mapToDomainModel() = InfoVirtualCard(status = status)

fun ValidateUserStatusQuery.Data.mapToDomainModel() = validateUserStatus.mapToDomainModel()
