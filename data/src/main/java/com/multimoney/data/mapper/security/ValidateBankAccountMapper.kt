package com.multimoney.data.mapper.security

import com.multimoney.data.networking.graphql.apollomodel.ValidateBankAccountQuery
import com.multimoney.domain.model.security.ValidateAccount

private fun ValidateBankAccountQuery.ValidateBankAccount.mapToDomainModel() = ValidateAccount(
    responseCode = responseCode,
    responseMessage = responseMessage,
    name = name,
    currency = currency,
    sellPriceDollar = sellPriceDollar,
    buyPriceDollar = buyPriceDollar,
    bankId = bankId,
    bankName = bankName
)

fun ValidateBankAccountQuery.Data.mapToDomainModel() = validateBankAccount.mapToDomainModel()
