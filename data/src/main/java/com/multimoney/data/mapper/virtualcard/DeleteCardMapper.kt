package com.multimoney.data.mapper.virtualcard

import com.multimoney.data.networking.graphql.apollomodel.DeleteCardVDMutation
import com.multimoney.domain.model.util.error.MessageError
import com.multimoney.domain.model.virtualcard.DeleteCard

private fun DeleteCardVDMutation.DeleteCardVD.mapMessageToDomainModel() = MessageError(
    status = status,
    message = message,
    detail = detail
)

private fun DeleteCardVDMutation.DeleteCardVD.mapToDomainModel() = DeleteCard(
    isApproved = isApproved,
    apiStatus = apiStatus,
    messageError = mapMessageToDomainModel()
)

fun DeleteCardVDMutation.Data.mapToDomainModel() = deleteCardVD.mapToDomainModel()