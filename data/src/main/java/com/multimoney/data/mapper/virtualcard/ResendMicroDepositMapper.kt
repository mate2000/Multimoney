package com.multimoney.data.mapper.virtualcard

import com.multimoney.data.networking.graphql.apollomodel.ResendMicroDepositVDMutation
import com.multimoney.domain.model.util.error.MessageError
import com.multimoney.domain.model.virtualcard.ResendMicroDepositVD

private fun ResendMicroDepositVDMutation.ResendMicroDepositVD.mapMessageToDomainModel() = MessageError(
    status = status,
    message = message,
    detail = ""
)

private fun ResendMicroDepositVDMutation.ResendMicroDepositVD.mapToDomainModel() = ResendMicroDepositVD(
    messageError = mapMessageToDomainModel()
)

fun ResendMicroDepositVDMutation.Data.mapToDomainModel() = resendMicroDepositVD.mapToDomainModel()
