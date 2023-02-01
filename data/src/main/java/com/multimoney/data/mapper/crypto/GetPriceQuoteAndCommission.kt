package com.multimoney.data.mapper.crypto

import com.multimoney.data.networking.graphql.apollomodel.GetPricesQuoteAndCommissionQuery
import com.multimoney.domain.model.crypto.PricesQuoteAndCommissionData
import com.multimoney.domain.model.crypto.PricesQuoteAndCommissions

fun GetPricesQuoteAndCommissionQuery.Data.mapToDomainModel() = PricesQuoteAndCommissionData(
    pricesQuote = pricesQuote.mapToDomainModel()
)

fun GetPricesQuoteAndCommissionQuery.PricesQuote.mapToDomainModel() = PricesQuoteAndCommissions(
    fee = fee,
    internal_fee = internal_fee.toString().toDouble(),
    taxAmount = taxAmount.toString().toDouble(),
    totalFee = totalFee.toString().toDouble(),
    quote_id = quote_id,
    side = side,
    price = price.toString().toDouble(),
    created_at = created_at,
    expires_at = expires_at,
    quote_amount = quote_amount.toString().toDouble(),
    base_amount = base_amount.toString().toDouble()
)