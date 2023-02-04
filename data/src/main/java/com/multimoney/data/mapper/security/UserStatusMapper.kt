package com.multimoney.data.mapper.security

import com.multimoney.data.networking.graphql.apollomodel.ValidateUserStatusQuery
import com.multimoney.domain.model.security.InfoBankAccount
import com.multimoney.domain.model.security.InfoCredit
import com.multimoney.domain.model.security.InfoCrypto
import com.multimoney.domain.model.security.InfoPreApprove
import com.multimoney.domain.model.security.InfoRequest
import com.multimoney.domain.model.security.InfoUser
import com.multimoney.domain.model.security.InfoVirtualCard
import com.multimoney.domain.model.security.Product
import com.multimoney.domain.model.security.ValidateUserStatus
import com.multimoney.domain.model.security.Wording

private fun ValidateUserStatusQuery.ValidateUserStatus.mapToDomainModel() = ValidateUserStatus(
    infoUser = infoUser.mapToDomainModel(),
    infoCredit = infoCredit.mapToDomainModel(),
    infoBankAccount = infoBankAccount.mapToDomainModel(),
    infoCrypto = infoCrypto.mapToDomainModel(),
    infoVirtualCard = infoVirtualCard.mapToDomainModel(),
    wording = infoUser.wording?.mapToDomainModel()
)

private fun ValidateUserStatusQuery.InfoUser.mapToDomainModel() = InfoUser(
    idBrand = idBrand.toString().toInt(),
    userName = userName,
    idClient = idClient.toString().toInt(),
    firstName = firstName,
    lastName = lastName,
    secondLastName = secondLastName,
    phone = phone,
    statusOnfido = statusOnfido
)

private fun ValidateUserStatusQuery.InfoCredit.mapToDomainModel() = InfoCredit(
    idClient = idClient.toString().toInt(),
    idLoanClient = idLoanClient.toString().toInt(),
    status = status,
    infoPreApprove = infoPreApprove?.mapToDomainModel(),
    wording = wording?.mapToDomainModel()
)

private fun ValidateUserStatusQuery.InfoPreApprove.mapToDomainModel() = InfoPreApprove(
    idUserRequest = idUserRequest.toString().toInt(),
    status = status,
    selectedAmount = selectedAmount.toString().toFloat(),
    statusFirm = statusFirm,
    currentStep = currentStep,
    infoProducts = infoProducts?.map { it.mapToDomainModel() },
    idPrint = idPrint?.toString()?.toLong() ?: 0,
    crosseling = crosseling
)

private fun ValidateUserStatusQuery.InfoProduct.mapToDomainModel() =
    Product(idProduct, amountAvailable.toString(), amountAvailableFormat, symbolCurrency)

private fun ValidateUserStatusQuery.InfoBankAccount.mapToDomainModel() = InfoBankAccount(
    statusFirm = statusFirm,
    status = status,
    infoRequest = infoRequest.mapToDomainModel(),
    wording = wording?.mapToDomainModel()
)

private fun ValidateUserStatusQuery.InfoCrypto.mapToDomainModel() = InfoCrypto(
    status = status,
    statusFirm = statusFirm,
    profileEnable = profileEnable,
    wording = wording?.mapToDomainModel()
)

private fun ValidateUserStatusQuery.InfoRequest.mapToDomainModel() = InfoRequest(
    idRequestSysde = idRequestSysde.toString().toLong(),
    idRequestGlobal = idRequestGlobal.toString().toLong(),
    currentStep = currentStep,
    statusRequest = statusRequest
)

private fun ValidateUserStatusQuery.Wording.mapToDomainModel() = Wording(
    textOne = textOne,
    textTwo = textTwo,
    cTA = cTA
)

private fun ValidateUserStatusQuery.Wording1.mapToDomainModel() = Wording(
    textOne = textOne,
    textTwo = textTwo,
    cTA = cTA,
    link = link.orEmpty(),
    display = display ?: false,
    workFlow = workFlow
)

private fun ValidateUserStatusQuery.Wording2.mapToDomainModel() = Wording(
    textOne = textOne,
    textTwo = textTwo,
    cTA = cTA
)

private fun ValidateUserStatusQuery.Wording3.mapToDomainModel() = Wording(
    textOne = textOne,
    textTwo = textTwo,
    cTA = cTA
)

private fun ValidateUserStatusQuery.InfoVirtualCard.mapToDomainModel() = InfoVirtualCard(status = status)

fun ValidateUserStatusQuery.Data.mapToDomainModel() = validateUserStatus.mapToDomainModel()
