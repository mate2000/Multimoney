package com.multimoney.data.mapper.smartaccount

import com.multimoney.data.networking.graphql.apollomodel.GetCoreBankMovementsQuery
import com.multimoney.domain.model.accountsmart.SmartMovement
import com.multimoney.domain.model.accountsmart.SmartMovementsResult

private fun GetCoreBankMovementsQuery.Result.mapToDomainModel(): SmartMovement {
    return SmartMovement(
        idTransaction.toString().toInt(),
        creationDate.toString(),
        transactionCatalogueDescription,
        amount.toString().toDouble(),
        currencyDescription ?: "",
        bankAuthorization ?: "",
        idSubTransaction.toString().toInt(),
        sign
    )
}

private fun GetCoreBankMovementsQuery.GetCoreBankMovements.mapToDomainModel() =
    SmartMovementsResult(
        totalRecords = totalRecords,
        result = result?.map { it.mapToDomainModel() } ?: emptyList()
    )

fun GetCoreBankMovementsQuery.Data.mapToDomainModel() = getCoreBankMovements?.mapToDomainModel()
