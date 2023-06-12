package com.multimoney.data.mapper.credit

import com.multimoney.data.networking.graphql.apollomodel.GetPromissoryNoteDetailQuery
import com.multimoney.domain.model.credit.CreditMovement
import com.multimoney.domain.model.credit.CreditMovementsResult
import com.multimoney.domain.model.credit.PromissoryNoteDetail

private fun GetPromissoryNoteDetailQuery.Values_Transaction.mapToDomainModel() =
    CreditMovement(
        description = tipo,
        amount = monto,
        amountLabel = monto_Label,
        transactionType = tipo_Transaccion,
        date = fecha_Aplicacion_Label
    )

private fun GetPromissoryNoteDetailQuery.Value.mapToDomainModel() =
    CreditMovementsResult(
        month = title,
        result = values_Transactions?.map { it.mapToDomainModel() }
    )

private fun GetPromissoryNoteDetailQuery.GetPromissoryNoteDetail.mapToDomainModel() =
    PromissoryNoteDetail(
        totalRecords = totalRecords,
        pageNumber = pageNumber,
        pageSize = pageSize,
        movementsResultList = values?.map { it.mapToDomainModel() }
    )

fun GetPromissoryNoteDetailQuery.Data.mapToDomainModel() = getPromissoryNoteDetail.mapToDomainModel()
