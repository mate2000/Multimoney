package com.multimoney.multimoney.presentation.navigation.navtype.payment

import android.os.Bundle
import androidx.navigation.NavType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.multimoney.domain.model.accountsmart.AccountSmartForBuyCrypto
import com.multimoney.domain.model.balance.Account

class SmartAccountForBuyCryptoNavType : NavType<List<AccountSmartForBuyCrypto>>(isNullableAllowed = false) {

    override fun get(bundle: Bundle, key: String): List<AccountSmartForBuyCrypto>? {
        return bundle.getParcelableArrayList(key)
    }

    override fun parseValue(value: String): List<AccountSmartForBuyCrypto> {
        val listType = object : TypeToken<ArrayList<Account?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    override fun put(bundle: Bundle, key: String, value: List<AccountSmartForBuyCrypto>) {
        bundle.putParcelableArray(key, value.toTypedArray())
    }
}
