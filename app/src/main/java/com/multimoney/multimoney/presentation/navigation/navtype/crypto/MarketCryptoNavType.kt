package com.multimoney.multimoney.presentation.navigation.navtype.crypto

import android.os.Bundle
import androidx.navigation.NavType
import com.google.gson.Gson
import com.multimoney.domain.model.crypto.MarketCryptoCoin

class MarketCryptoNavType : NavType<MarketCryptoCoin?>(isNullableAllowed = true) {
    override fun get(bundle: Bundle, key: String): MarketCryptoCoin? {
        return bundle.getParcelable(key)
    }

    override fun parseValue(value: String): MarketCryptoCoin? {
        return Gson().fromJson(value, MarketCryptoCoin::class.java)
    }

    override fun put(bundle: Bundle, key: String, value: MarketCryptoCoin?) {
        bundle.putParcelable(key, value)
    }
}
