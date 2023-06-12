package com.multimoney.data.mapper.credit

import com.multimoney.data.networking.graphql.apollomodel.PaymentAmountQuery
import com.multimoney.domain.model.credit.PaymentAmount

private fun PaymentAmountQuery.PaymentAmount.mapToDomainModel() = PaymentAmount(
    paymentAmount = paymentAmount.toString().toDouble(),
    paymentAmountLabel = paymentAmountLabel
)

fun PaymentAmountQuery.Data.mapToDomainModel() = paymentAmount.mapToDomainModel()
