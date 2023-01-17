package com.multimoney.multimoney.presentation.navigation.navtype.crypto

import android.os.Bundle
import androidx.navigation.NavType
import com.google.gson.Gson
import com.multimoney.domain.model.balance.BalanceCryptoAccountItems
import com.multimoney.domain.model.virtualcard.CardVisaDirect

class CryptoCurrencyNavType : NavType<BalanceCryptoAccountItems>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): BalanceCryptoAccountItems? {
        return bundle.getParcelable(key)
    }

    override fun parseValue(value: String): BalanceCryptoAccountItems {
        return Gson().fromJson(value, BalanceCryptoAccountItems::class.java)
    }

    override fun put(bundle: Bundle, key: String, value: BalanceCryptoAccountItems) {
        bundle.putParcelable(key, value)
    }
}
