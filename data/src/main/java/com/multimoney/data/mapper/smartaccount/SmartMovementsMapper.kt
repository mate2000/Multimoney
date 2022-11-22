package com.multimoney.data.mapper.smartaccount

import com.multimoney.data.networking.graphql.apollomodel.GetCoreBankMovementsQuery
import com.multimoney.domain.model.accountsmart.SmartMovement
import com.multimoney.domain.model.accountsmart.SmartMovementsResult

private fun GetCoreBankMovementsQuery.Result.mapToDomainModel(): SmartMovement {
    return SmartMovement(
        idTransaction as Int,
        creationDate as String,
        transactionCatalogueDescription ?: "",
        amount.toString().toDouble(),
        currencyDescription ?: "",
        bankAuthorization ?: "",
        idSubTransaction as Int,
        sign ?: ""
    )
}

private fun GetCoreBankMovementsQuery.GetCoreBankMovements.mapToDomainModel() =
    SmartMovementsResult(result = result.map { it.mapToDomainModel() })

fun GetCoreBankMovementsQuery.Data.mapToDomainModel() = getCoreBankMovements?.mapToDomainModel()
