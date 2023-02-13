package com.multimoney.multimoney.presentation.navigation.navtype.crypto

import android.os.Bundle
import androidx.navigation.NavType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.multimoney.domain.model.balance.BalanceCryptoAccountItems

class UserCryptoBalancesNavType : NavType<List<BalanceCryptoAccountItems>>(isNullableAllowed = false) {

    override fun get(bundle: Bundle, key: String): List<BalanceCryptoAccountItems>? {
        return bundle.getParcelableArrayList(key)
    }

    override fun parseValue(value: String): List<BalanceCryptoAccountItems> {
        val listType = object : TypeToken<ArrayList<BalanceCryptoAccountItems?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    override fun put(bundle: Bundle, key: String, value: List<BalanceCryptoAccountItems>) {
        bundle.putParcelableArray(key, value.toTypedArray())
    }
}