package com.multimoney.domain.model.crypto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MarketCryptoCoin(
    val description: String,
    val baseAsset: String,
    val amountchange: String,
    val percentChange: String,
    val priority: Int,
    val currentPrice: Double,
    val url_image: String,
    val historico: Boolean,
    val cryptoNetwork: String
) : Parcelable
