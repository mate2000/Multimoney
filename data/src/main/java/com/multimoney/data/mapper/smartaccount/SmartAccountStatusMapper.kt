package com.multimoney.data.mapper.smartaccount

import com.multimoney.data.networking.graphql.apollomodel.UpdateSmartAccountStatusMutation
import com.multimoney.domain.model.accountsmart.SmartAccountStatusResult

private fun UpdateSmartAccountStatusMutation.Result.mapToDomain() = SmartAccountStatusResult(
    this.requestID.toString().toInt(),
    this.message,
    this.status,
    this.urlFirmDocument
)

private fun UpdateSmartAccountStatusMutation.UpdateSmartAccountStatus.mapToDomain() =
    this.result.mapToDomain()

fun UpdateSmartAccountStatusMutation.Data.mapToDomain() = this.updateSmartAccountStatus?.mapToDomain()