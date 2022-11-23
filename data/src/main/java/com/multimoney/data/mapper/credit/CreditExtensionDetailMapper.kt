package com.multimoney.data.mapper.credit

import com.multimoney.data.networking.graphql.apollomodel.SaveCreditExtensionDetailMutation
import com.multimoney.domain.model.credit.CreditExtensionDetail
import com.multimoney.domain.model.util.error.MessageError

private fun SaveCreditExtensionDetailMutation.SaveCreditExtensionDetail.mapMessageToDomainModel() = MessageError(
    status = status,
    message = message,
    detail = detail
)

private fun SaveCreditExtensionDetailMutation.SaveCreditExtensionDetail.mapToDomainModel() = CreditExtensionDetail(
    comissionDisbursement = comision_Desembolso,
    quotaTotal = cuota_Total,
    quotaMaximum = cuota_Maxima,
    selectedAmount = monto_Seleccionado,
    nextPayment = proximo_Pago.toString(),
    messageError = mapMessageToDomainModel()
)

fun SaveCreditExtensionDetailMutation.Data.mapToDomainModel() = saveCreditExtensionDetail?.mapToDomainModel()
