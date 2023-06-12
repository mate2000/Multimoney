package com.multimoney.data.mapper.crypto

import com.multimoney.data.networking.graphql.apollomodel.GetTransferCommissionQuery
import com.multimoney.domain.model.crypto.GetTransferFee
import com.multimoney.domain.model.crypto.GetTransferFeeData

private fun GetTransferCommissionQuery.TransferFee.mapToDomainModel() = GetTransferFee(
    id = id,
    fee = fee,
    internalFee = internal_fee.toString().toDouble(),
    taxAmount = taxAmount.toString().toDouble(),
    expiresAt = expires_at,
    totalFee = totalFee.toString().toDouble()
)

fun GetTransferCommissionQuery.Data.mapToDomainModel() = GetTransferFeeData(
    transferFee = transferFee.mapToDomainModel()
)