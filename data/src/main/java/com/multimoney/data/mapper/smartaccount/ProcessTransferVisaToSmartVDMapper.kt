package com.multimoney.data.mapper.smartaccount

import com.multimoney.data.networking.graphql.apollomodel.ProcessTransferVisaToSmartVDMutation
import com.multimoney.domain.model.accountsmart.VisaSmartPayment
import com.multimoney.domain.model.util.error.MessageError

private fun ProcessTransferVisaToSmartVDMutation.ProcessTransferVisaToSmartVD.mapToDomainModel() =
    VisaSmartPayment(
        referenceNumber = numeroReferencia,
        referenceNumberVisa = numeroReferenciaVisa,
        messageError = MessageError(
            status = status,
            message = message,
            detail = detail
        )
    )

fun ProcessTransferVisaToSmartVDMutation.Data.mapToDomainModel() = processTransferVisaToSmartVD.mapToDomainModel()
