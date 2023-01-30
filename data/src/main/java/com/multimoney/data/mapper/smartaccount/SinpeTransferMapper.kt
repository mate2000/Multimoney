package com.multimoney.data.mapper.smartaccount

import com.multimoney.data.networking.graphql.apollomodel.ProcessSinpeTransferMutation
import com.multimoney.domain.model.accountsmart.SinpeTransferResult
import com.multimoney.domain.model.util.error.MessageError

//private fun ProcessSinpeTransferMutation.Result.mapToDomainModel() = SinpeTransferResult(
//    referenceNumber = nUMERO_REFERENCIA_INTERNO
//)

private fun ProcessSinpeTransferMutation.SinpeTransfer.mapToDomainModel(): SinpeTransferResult {
    return SinpeTransferResult(
        referenceNumber = result?.nUMERO_REFERENCIA_INTERNO ?: "",
        messageError = MessageError(
            status = status,
            message = message,
            detail = "placeholder"
        )
    )
}

fun ProcessSinpeTransferMutation.Data.mapToDomainModel() = sinpeTransfer?.mapToDomainModel()
