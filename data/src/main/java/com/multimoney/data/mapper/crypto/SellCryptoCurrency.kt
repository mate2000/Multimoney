package com.multimoney.data.mapper.crypto

import com.multimoney.data.networking.graphql.apollomodel.SellCryptoCurrencyMutation
import com.multimoney.domain.model.crypto.SellCryptoCurrencyHQRData
import com.multimoney.domain.model.crypto.SellCryptoCurrencyResult
import com.multimoney.domain.model.crypto.SellHQRResponse

private fun SellCryptoCurrencyMutation.Result.mapToDomainModel() = SellCryptoCurrencyResult(
    sysdeTransactionNumber = sysdeTransactionNumber,
    paxosSendTransferId = paxosSendTransferId,
    paxosReceivedTransferId = paxosReceivedTransferId,
    paxosIdOrder = paxosIdOrder,
    orderComplete = orderComplete,
    filledAmount = filledAmount.toString().toDouble(),
    averagePrice = averagePrice.toString().toDouble(),
    status = status,
    taxAmount = taxAmount.toString().toDouble(),
    comissionAmount = comissionAmount.toString().toDouble(),
)

private fun SellCryptoCurrencyMutation.SellHQR.mapToDomainModel() = SellHQRResponse(
    status = status,
    message = message,
    result = result?.mapToDomainModel()
)

fun SellCryptoCurrencyMutation.Data.mapToDomainModel() = SellCryptoCurrencyHQRData(
    sellHQRResponse = sellHQR.mapToDomainModel()
)