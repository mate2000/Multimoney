package com.multimoney.data.mapper.crypto

import com.multimoney.data.networking.graphql.apollomodel.BuyCryptoCurrencyHQRMutation
import com.multimoney.domain.model.crypto.BuyCryptoCurrencyData
import com.multimoney.domain.model.crypto.BuyCryptoCurrencyResponse
import com.multimoney.domain.model.crypto.BuyCryptoCurrencyResult

fun BuyCryptoCurrencyHQRMutation.Data.mapToDomainModel() = BuyCryptoCurrencyData(
    buyHQR = buyHQR.mapToDomainModel()
)

fun BuyCryptoCurrencyHQRMutation.BuyHQR.mapToDomainModel() = BuyCryptoCurrencyResponse(
    status = status,
    message = message,
    result = result?.mapToDomainModel()
)

fun BuyCryptoCurrencyHQRMutation.Result.mapToDomainModel() = BuyCryptoCurrencyResult(
    sysdeTransactionNumber = sysdeTransactionNumber,
    paxosSendTransferId = paxosSendTransferId,
    paxosReceivedTransferId = paxosReceivedTransferId,
    paxosIdOrder = paxosIdOrder,
    orderComplete = orderComplete,
    filledAmount = filledAmount.toString().toDouble(),
    averagePrice = averagePrice.toString().toDouble(),
    status = status,
    taxAmount = taxAmount.toString().toDouble(),
    commissionAmount = commissionAmount.toString().toDouble()
)