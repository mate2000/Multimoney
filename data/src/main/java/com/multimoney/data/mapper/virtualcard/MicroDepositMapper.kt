package com.multimoney.data.mapper.virtualcard

import com.multimoney.data.networking.graphql.apollomodel.MicroDepositVDMutation
import com.multimoney.domain.model.util.error.MessageError
import com.multimoney.domain.model.virtualcard.MicroDepositVD

private fun MicroDepositVDMutation.MicroDepositVD.mapMessageToDomainModel() = MessageError(
    status = status,
    message = message,
    detail = ""
)

private fun MicroDepositVDMutation.MicroDepositVD.mapToDomainModel() = MicroDepositVD(
    messageError = mapMessageToDomainModel()
)

fun MicroDepositVDMutation.Data.mapToDomainModel() = microDepositVD.mapToDomainModel()
