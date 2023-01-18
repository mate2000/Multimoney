package com.multimoney.data.mapper.crypto

import com.multimoney.data.networking.graphql.apollomodel.BuyCryptoCurrencyMutation
import com.multimoney.domain.model.crypto.BuyCryptoCurrencyOrder
import com.multimoney.domain.model.crypto.BuyCryptoCurrencyResult

fun BuyCryptoCurrencyMutation.Data.mapToDomainModel() = buyOrder.mapToDomainModel()

private fun BuyCryptoCurrencyMutation.BuyOrder.mapToDomainModel() = BuyCryptoCurrencyOrder(
    result = result.mapToDomainModel()
)

private fun BuyCryptoCurrencyMutation.Result.mapToDomainModel() = BuyCryptoCurrencyResult(
    sysdeTransactionNumber = sysdeTransactionNumber,
    paxosSendTransferId = paxosSendTransferId,
    paxosIdOrder = paxosIdOrder,
    paxosReceivedTransferId = paxosReceivedTransferId,
    orderComplete = orderComplete,
    filledAmount = filledAmount.toString().toDouble(),
    averagePrice = averagePrice.toString().toDouble(),
    status = status,
    taxAmount = taxAmount.toString().toDouble(),
    commissionAmount = commissionAmount.toString().toDouble()
)