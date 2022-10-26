package com.multimoney.data.mapper.security

import com.multimoney.data.networking.security.apollomodel.ValidateBankAccountQuery
import com.multimoney.data.networking.security.apollomodel.ValidatePinQuery
import com.multimoney.domain.model.security.ValidateAccount
import com.multimoney.domain.model.security.ValidatePin
import com.multimoney.domain.model.util.error.MessageError

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

fun ValidateBankAccountQuery.Data.mapToDomainModel() = validateBankAccount?.mapToDomainModel()
