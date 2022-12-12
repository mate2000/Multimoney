package com.multimoney.data.mapper.credit

import com.multimoney.data.networking.graphql.apollomodel.CreditExtensionAmountQuery
import com.multimoney.domain.model.credit.CreditExtensionAmount

private fun CreditExtensionAmountQuery.CreditExtensionAmount.mapToDomainModel() = CreditExtensionAmount(
    amountMax = amountMax.toString().toDouble(),
    amountMin = amountMin.toString().toDouble(),
    labelAmountMaxAvailable = labelAmountMaxAvailable,
    labelAmountMinAvailable = labelAmountMinAvailable,
    symbol = symbol,
    amountTract = amountTract,
    idLoanClient = idLoanClient.toString().toLong(),
    idProductBase = idProductBase,
    cicle = cicle,
    quotaMax = quotaMax.toString().toDouble(),
    promissoryNote = promissoryNote,
    amountMinDisbursementCK = amountMinDisbursementCK.toString().toDouble(),
    paramMore = paramMore
)

fun CreditExtensionAmountQuery.Data.mapToDomainModel() = creditExtensionAmount.mapToDomainModel()
