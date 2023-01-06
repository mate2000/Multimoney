package com.multimoney.domain.model.crypto

data class CryptoNewsFeed(
    val information: String,
    val result: List<New>,
    val title: String
)