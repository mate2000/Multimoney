package com.multimoney.data.mapper.credit

import com.multimoney.data.networking.graphql.apollomodel.ProcessCreditExtensionDetailMutation
import com.multimoney.domain.model.credit.ProcessCreditExtensionDetail
import com.multimoney.domain.model.util.error.MessageError

private fun ProcessCreditExtensionDetailMutation.ProcessCreditExtensionDetail.mapMessageToDomainModel() = MessageError(
    status = status,
    message = message,
    detail = detail
)

private fun ProcessCreditExtensionDetailMutation.ProcessCreditExtensionDetail.mapToDomainModel() = ProcessCreditExtensionDetail(
    reference = pagare,
    loanId = id_Prestamo,
    messageError = mapMessageToDomainModel()
)

fun ProcessCreditExtensionDetailMutation.Data.mapToDomainModel() = processCreditExtensionDetail?.mapToDomainModel()
