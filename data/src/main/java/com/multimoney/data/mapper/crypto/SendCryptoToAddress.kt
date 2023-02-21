package com.multimoney.data.mapper.crypto

import com.multimoney.data.networking.graphql.apollomodel.SendCryptoToAddressMutation
import com.multimoney.domain.model.crypto.SendCryptoToAddressData
import com.multimoney.domain.model.crypto.SendCryptoToAddressOrder
import com.multimoney.domain.model.crypto.SendCryptoToAddressResult

private fun SendCryptoToAddressMutation.Result.mapToDomainModel() = SendCryptoToAddressResult(
    sysdeTransactionNumber = sysdeTransactionNumber,
    paxosIdOrder = paxosIdOrder,
    paxosReceivedTransferId = paxosReceivedTransferId,
    paxosSendTransferId = paxosSendTransferId,
    steps = steps,
    orderComplete = orderComplete,
    filledAmount = filledAmount.toString().toDouble(),
    averagePrice = averagePrice.toString().toDouble(),
    status = status,
    commissionAmount = commissionAmount.toString().toDouble(),
    totalAmount = totalAmount.toString().toDouble()
)

private fun SendCryptoToAddressMutation.TransferOrder.mapToDomainModel() = SendCryptoToAddressOrder(
    result = result.mapToDomainModel(),
    status = status,
    message = message,
    detail = detail
)

fun SendCryptoToAddressMutation.Data.mapToDomainModel() = SendCryptoToAddressData(
    transferOrder = transferOrder.mapToDomainModel()
)