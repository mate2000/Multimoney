package com.multimoney.data.mapper.credit

import com.multimoney.data.networking.graphql.apollomodel.GetPaymentPointsQuery
import com.multimoney.domain.model.credit.PaymentPoint

private fun GetPaymentPointsQuery.GetPaymentPoint.mapToDomainModel() = PaymentPoint(
    name = name,
    description = description,
    address = address,
    addressDescription = addressDescription,
    schedule = schedule
)

fun GetPaymentPointsQuery.Data.mapToDomainModel() = getPaymentPoints.map { it.mapToDomainModel() }
