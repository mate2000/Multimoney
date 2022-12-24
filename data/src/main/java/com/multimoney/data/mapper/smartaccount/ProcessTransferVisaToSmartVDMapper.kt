package com.multimoney.data.mapper.smartaccount

import com.multimoney.data.networking.graphql.apollomodel.ProcessTransferVisaToSmartVDMutation
import com.multimoney.domain.model.accountsmart.VisaSmartPayment

private fun ProcessTransferVisaToSmartVDMutation.ProcessTransferVisaToSmartVD.mapToDomainModel() =
    VisaSmartPayment(
        referenceNumber = numeroReferencia,
        referenceNumberVisa = numeroReferenciaVisa
    )

fun ProcessTransferVisaToSmartVDMutation.Data.mapToDomainModel() = processTransferVisaToSmartVD.mapToDomainModel()
