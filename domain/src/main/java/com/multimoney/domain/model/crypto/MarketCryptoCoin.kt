package com.multimoney.domain.model.crypto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MarketCryptoCoin(
    val description: String,
    val baseAsset: String,
    val amountchange: String? = null,
    val percentChange: String? = null,
    val priority: Int? = null,
    val currentPrice: Double? = null,
    val url_image: String,
    val historico: Boolean? = null,
    val cryptoNetwork: String
) : Parcelable
