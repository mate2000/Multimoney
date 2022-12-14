package com.multimoney.data.mapper.smartaccount

import com.multimoney.data.networking.graphql.apollomodel.AccountSmartContractEventSubscription
import com.multimoney.domain.model.accountsmart.AccountSmartContractResult

fun AccountSmartContractEventSubscription.AccountSmartContractEvent.mapToDomain() =
    AccountSmartContractResult(
        idRequestSysde.toString().toLong(),
        idBrand,
        link,
        statusEvicertia,
        statusOnfido,
        active,
        currentStep
    )

fun AccountSmartContractEventSubscription.Data.mapToDomain() =
    accountSmartContractEvent.mapToDomain()