package com.multimoney.data.mapper.credit

import com.multimoney.data.networking.graphql.apollomodel.SendCreditContractEventMutation
import com.multimoney.domain.model.credit.CreditContractEvent

private fun SendCreditContractEventMutation.SaveCreditContract.mapToDomainModel() = CreditContractEvent(
    idPrint = idImpresion.toString().toLong(),
    idBrand = idBrand,
    link = link,
    statusEvicertia = statusEvicertia,
    statusOnfido = statusOnfido,
    active = active,
    currentStep = currentStep
)

fun SendCreditContractEventMutation.Data.mapToDomainModel() = saveCreditContract?.mapToDomainModel()
