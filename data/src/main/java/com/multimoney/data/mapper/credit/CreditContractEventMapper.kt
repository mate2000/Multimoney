package com.multimoney.data.mapper.credit

import com.multimoney.data.networking.graphql.apollomodel.CreditContractEventSubscription
import com.multimoney.domain.model.credit.CreditContractEvent

private fun CreditContractEventSubscription.CreditContractEvent.mapToDomainModel() = CreditContractEvent(
    idPrint = idPrint.toString().toLong(),
    idBrand = idBrand,
    link = link,
    statusEvicertia = statusEvicertia,
    statusOnfido = statusOnfido,
    active = active,
    currentStep = currentStep
)

fun CreditContractEventSubscription.Data.mapToDomainModel() = creditContractEvent.mapToDomainModel()
