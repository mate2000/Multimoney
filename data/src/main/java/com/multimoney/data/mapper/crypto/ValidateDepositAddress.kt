package com.multimoney.data.mapper.crypto

import com.multimoney.data.networking.graphql.apollomodel.ValidateDepositAddressMutation
import com.multimoney.domain.model.crypto.ValidateDepositAddressResponse
import com.multimoney.domain.model.crypto.ValidateDepositAddressResult

fun ValidateDepositAddressMutation.Data.mapToDomainModel() = ValidateDepositAddressResponse(
    status = validateDepositAddress.status,
    message = validateDepositAddress.message,
    result = validateDepositAddress.result?.mapToDomainModel()
)

fun ValidateDepositAddressMutation.Result.mapToDomainModel() = ValidateDepositAddressResult(
    page = page,
    totalPages = totalPages,
    itemsOnPage = itemsOnPage,
    address = address.orEmpty(),
    balance = balance.orEmpty(),
    unconfirmedBalance = unconfirmedBalance.orEmpty(),
    unconfirmedTxs = unconfirmedTxs,
    txs = txs,
    nonTokenTxs = nonTokenTxs
)
