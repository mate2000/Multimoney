package com.multimoney.data.mapper.mmvisa

import com.multimoney.data.networking.graphql.apollomodel.CardIssuanceNVQuery

fun CardIssuanceNVQuery.CardIssuanceNV.mapToDomainModel() = com.multimoney.domain.model.mmvisa.CardIssuanceNV(
    rc = rc,
    msg = msg,
    processAt = processedAt
)

fun CardIssuanceNVQuery.Data.mapToDomainModel() = cardIssuanceNV.mapToDomainModel()
