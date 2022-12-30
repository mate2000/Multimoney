package com.multimoney.data.mapper.crypto

import com.multimoney.data.networking.graphql.apollomodel.GetCryptoCurrencyMovementQuery
import com.multimoney.domain.model.crypto.CryptoCurrencyMovement
import com.multimoney.domain.model.crypto.Item

fun GetCryptoCurrencyMovementQuery.CryptoCurrencyMovement.mapToDomainModel() =
    CryptoCurrencyMovement(
        items.map {
            it.mapToDomainModel()
        },
        total_count = total_count
    )

private fun GetCryptoCurrencyMovementQuery.Item.mapToDomainModel() =
    Item(
        abbreviationCurrency = abbreviationCurrency,
        amount_filled = amount_filled,
        base_amount = base_amount,
        created_at = created_at as String,
        dateCreated = dateCreated,
        descriptionMovement = decriptionMovement,
        held = held,
        id = id,
        market = market,
        modified_at = modified_at as String,
        month_limit_exceeded = month_limit_exceeded,
        price = price,
        profile_id = profile_id,
        quote_amount = quote_amount,
        side = side,
        type = type,
        volume_weighted_average_price = volume_weighted_average_price
    )