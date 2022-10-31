package com.multimoney.data.mapper.credit

import com.multimoney.data.networking.credit.apollomodel.ProccessPaymentListMutation
import com.multimoney.domain.model.credit.ProcessPaymentList

fun ProccessPaymentListMutation.Data.mapToDomainModel() = proccessPaymentList?.mapToDomainModel()

fun ProccessPaymentListMutation.ProccessPaymentList.mapToDomainModel() = ProcessPaymentList(
    status = status.toString(),
    message = message.toString(),
    detail = detail.toString(),
    rejectCodeSinpe = cODIGO_RECHAZO_SINPE,
    rejectCauseSinpe = mOTIVO_RECHAZO_SINPE,
    responseMessage = mENSAJE_RESPUESTA,
    internResponseMessage = mENSAJE_RESPUESTA_INTERNO,
    internReferenceNumber = nUMERO_REFERENCIA_INTERNO,
    referenceNumberSinpe = nUM_REF_SINPE

)
