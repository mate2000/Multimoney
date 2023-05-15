package com.multimoney.data.mapper.credit

import com.multimoney.data.networking.graphql.apollomodel.BankAccountTypesQuery
import com.multimoney.domain.model.credit.RegularExpression
import com.multimoney.domain.model.credit.RegularExpressionList

private fun BankAccountTypesQuery.RegularExpression.mapToDomainModel() = RegularExpression(
    regularExpression,
    key,
    description,
    expression,
    foreignKey,
    disbursementAccount
)

fun BankAccountTypesQuery.Data.mapToDomainModel() =
    RegularExpressionList(regularExpression.map { it.mapToDomainModel() })