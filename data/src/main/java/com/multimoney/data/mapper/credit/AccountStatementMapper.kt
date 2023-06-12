package com.multimoney.data.mapper.credit

import com.multimoney.data.networking.graphql.apollomodel.AccountStatementQuery
import com.multimoney.domain.model.credit.AccountStatement

private fun AccountStatementQuery.AccountStatement.mapToDomainModel() =
    AccountStatement(
        bytePdf = bytePDF.map { it.toString().toInt().toByte() }.toByteArray()
    )

fun AccountStatementQuery.Data.mapToDomainModel() = accountStatement.mapToDomainModel()
