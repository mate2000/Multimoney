package com.multimoney.domain.model.security

data class MiniCards(val miniCardsList: List<MiniCardsItem>)

data class MiniCardsItem(
    val priority: Int,
    val type: String,
    val imageUrl: String,
    val deepLink: String,
    val flow: String
)
