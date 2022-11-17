package com.multimoney.data.mapper.credit

import com.multimoney.data.networking.graphql.apollomodel.GetPaymentPointsQuery
import com.multimoney.domain.model.credit.PaymentPoint

// TODO: MAP THE LATITUDE AND LONGITUDE WHEN IS RETURNED FROM API
private fun GetPaymentPointsQuery.GetPaymentPoint.mapToDomainModel() = PaymentPoint(
    name = name,
    description = description,
    address = address,
    addressDescription = addressDescription,
    schedule = schedule,
    latitude = "13.7013318",
    longitude = "-89.2266226"
)

fun GetPaymentPointsQuery.Data.mapToDomainModel() = getPaymentPoints.map { it.mapToDomainModel() }
