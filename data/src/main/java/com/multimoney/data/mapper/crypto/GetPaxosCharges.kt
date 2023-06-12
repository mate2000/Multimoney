package com.multimoney.data.mapper.crypto

import com.multimoney.data.networking.graphql.apollomodel.GetPaxosChargesQuery
import com.multimoney.domain.model.crypto.GetPaxosChargeData
import com.multimoney.domain.model.crypto.GetPaxosChargeItem

fun GetPaxosChargesQuery.Data.mapToDomainModel() = GetPaxosChargeData(
    paxosChargeList = cryptoChargeList.map { it.mapToDomainModel() }
)

private fun GetPaxosChargesQuery.CryptoChargeList.mapToDomainModel() = GetPaxosChargeItem(
    id = id,
    transaction = transaction,
    key = key,
    startAmount = startAmount.toString().toDouble(),
    finalAmount = finalAmount.toString().toDouble(),
    value = value.toString().toDouble(),
    type = type
)