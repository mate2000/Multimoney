package com.multimoney.data.mapper.crypto

import com.multimoney.data.networking.graphql.apollomodel.GetCryptoMovementsQuery
import com.multimoney.domain.model.crypto.CryptoCurrencyMovement
import com.multimoney.domain.model.crypto.CryptoCurrencyMovements
import com.multimoney.domain.model.crypto.GetCryptoCurrencyMovements

fun GetCryptoMovementsQuery.Data.mapToDomainModel() = GetCryptoCurrencyMovements(
    cryptoCurrencyMovements = cryptoCurrencyMovement.mapToDomainModel()
)

private fun GetCryptoMovementsQuery.CryptoCurrencyMovement.mapToDomainModel() = CryptoCurrencyMovements(
    total_count = total_count,
    items = items.map { it.mapToDomainModel() }
)

private fun GetCryptoMovementsQuery.Item.mapToDomainModel() = CryptoCurrencyMovement(
    abbreviationCurrency = abbreviationCurrency,
    amount_filled = amount_filled,
    base_amount = base_amount,
    dateCreated = dateCreated,
    created_at = created_at.toString(),
    held = held,
    market = market,
    modified_at = modified_at.toString(),
    id = id,
    descriptionMovement = decriptionMovement,
    price = price,
    profile_id = profile_id,
    quote_amount = quote_amount,
    side = side,
    type = type,
    month_limit_exceeded = month_limit_exceeded,
    volume_weighted_average_price = volume_weighted_average_price
)