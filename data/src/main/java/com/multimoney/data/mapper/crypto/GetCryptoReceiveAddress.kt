package com.multimoney.data.mapper.crypto

import com.multimoney.data.networking.graphql.apollomodel.GetCryptoReceiveAddressQuery
import com.multimoney.domain.model.crypto.CryptoReceiveAddress
import com.multimoney.domain.model.crypto.GetCryptoReceiveAddressData

fun GetCryptoReceiveAddressQuery.Data.mapToDomainModel() = GetCryptoReceiveAddressData (
    cryptoReceiveAddress = depositAddress.mapToDomainModel()
)

fun GetCryptoReceiveAddressQuery.DepositAddress.mapToDomainModel() = CryptoReceiveAddress(
    id = id,
    address = address,
    crypto_network = crypto_network
)