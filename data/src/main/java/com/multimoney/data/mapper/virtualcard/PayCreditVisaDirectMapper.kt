package com.multimoney.data.mapper.virtualcard

import com.multimoney.data.networking.graphql.apollomodel.PayCreditVDMutation
import com.multimoney.domain.model.util.error.MessageError
import com.multimoney.domain.model.virtualcard.PayCreditVisaDirect

private fun PayCreditVDMutation.PayCreditVD.mapMessageToDomainModel() = MessageError(
    status = status,
    message = message,
    detail = detail
)

private fun PayCreditVDMutation.PayCreditVD.mapToDomainModel() = PayCreditVisaDirect(
    paymentDocument = documentoPay,
    paymentStatus = statusPay,
    paymentAmount = amountPay.toString().toDouble(),
    ticket = ticket,
    referenceAuthorization = referenceAuthotization,
    nextPayDate = nextPayDate.toString(),
    quotaNumber = quota_Number.toString().toLong(),
    observations = observations,
    referenceAuthorizationVisa = referenceAuthotizationVisa,
    messageError = mapMessageToDomainModel()
)

fun PayCreditVDMutation.Data.mapToDomainModel() = payCreditVD.mapToDomainModel()
