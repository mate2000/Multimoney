package com.multimoney.data.mapper.security

import com.multimoney.data.networking.graphql.apollomodel.UserEventMobileSaveMutation
import com.multimoney.domain.model.security.UserEventMobileSave

private fun UserEventMobileSaveMutation.UserEventMobileSave.mapToDomainModel() = UserEventMobileSave(
    status = status,
    message = message,
    detail = detail
)

fun UserEventMobileSaveMutation.Data.mapToDomainModel() = userEventMobileSave.mapToDomainModel()
