package com.multimoney.data.mapper.crypto

import com.multimoney.data.networking.graphql.apollomodel.ValidateDepositAddressMutation
import com.multimoney.domain.model.crypto.ValidateDepositAddressResult
import com.multimoney.domain.model.crypto.ValidateDepositAddressResponse

fun ValidateDepositAddressMutation.Data.mapToDomainModel() = ValidateDepositAddressResponse(
    status = validateDepositAddress.status,
    message = validateDepositAddress.message,
    result = validateDepositAddress.result?.mapToDomainModel()
)

fun ValidateDepositAddressMutation.Result.mapToDomainModel() = ValidateDepositAddressResult(
    page = page,
    totalPages = totalPages,
    itemsOnPage = itemsOnPage,
    address = address,
    balance = balance,
    unconfirmedBalance = unconfirmedBalance,
    unconfirmedTxs = unconfirmedTxs,
    txs = txs,
    nonTokenTxs = nonTokenTxs
)
