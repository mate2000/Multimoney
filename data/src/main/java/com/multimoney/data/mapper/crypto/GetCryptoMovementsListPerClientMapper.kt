package com.multimoney.data.mapper.crypto

import com.multimoney.data.networking.graphql.apollomodel.CryptoCurrencyMovementQuery
import com.multimoney.domain.model.crypto.CryptoCurrencyMovement
import com.multimoney.domain.model.crypto.Item

private fun CryptoCurrencyMovementQuery.CryptoCurrencyMovement.mapToDomainModel() =
    CryptoCurrencyMovement(
        total_count = total_count,
        items = items.map { it.mapToDomainModel() }
    )

private fun CryptoCurrencyMovementQuery.Item.mapToDomainModel() = Item(
    abbreviationCurrency = abbreviationCurrency,
    amount_filled = amount_filled,
    base_amount = base_amount,
    created_at = created_at.toString(),
    dateCreated = dateCreated,
    descriptionMovement = decriptionMovement,
    held = held,
    id = id,
    market = market,
    modified_at = modified_at.toString(),
    month_limit_exceeded = month_limit_exceeded,
    price = price,
    profile_id = profile_id,
    quote_amount = quote_amount,
    side = side,
    type = type,
    volume_weighted_average_price = volume_weighted_average_price
)

fun CryptoCurrencyMovementQuery.Data.mapToDomainModel() = cryptoCurrencyMovement.mapToDomainModel()