package com.multimoney.data.mapper.smartaccount

import com.multimoney.data.networking.graphql.apollomodel.ProcessSinpeTransferMutation
import com.multimoney.domain.model.accountsmart.SinpeTransferResult

private fun ProcessSinpeTransferMutation.Result.mapToDomainModel() = SinpeTransferResult(
    referenceNumber = nUMERO_REFERENCIA_INTERNO
)

private fun ProcessSinpeTransferMutation.SinpeTransfer.mapToDomainModel() = this.result?.mapToDomainModel()

fun ProcessSinpeTransferMutation.Data.mapToDomainModel() = sinpeTransfer?.mapToDomainModel()