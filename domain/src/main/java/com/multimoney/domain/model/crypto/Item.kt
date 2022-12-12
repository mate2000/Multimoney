package com.multimoney.domain.model.crypto

data class Item(
    val abbreviationCurrency: String,
    val amount_filled: String,
    val base_amount: String,
    val created_at: String,
    val dateCreated: String,
    val descriptionMovement: String,
    val held: Boolean,
    val id: String,
    val market: String,
    val modified_at: String,
    val month_limit_exceeded: Boolean,
    val price: String,
    val profile_id: String,
    val quote_amount: String,
    val side: String,
    val type: String,
    val volume_weighted_average_price: String
)