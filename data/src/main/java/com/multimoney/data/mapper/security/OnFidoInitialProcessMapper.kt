package com.multimoney.data.mapper.security

import com.multimoney.data.networking.security.apollomodel.OnfidoIntialProcessMutation
import com.multimoney.domain.model.security.OnfidoToken

private fun OnfidoIntialProcessMutation.OnfidoInitialProcess.mapToDomainModel() = OnfidoToken(
    applicantId = applicantId,
    sdkToken = sdkToken
)

fun OnfidoIntialProcessMutation.Data.mapToDomainModel() = onfidoInitialProcess?.mapToDomainModel()