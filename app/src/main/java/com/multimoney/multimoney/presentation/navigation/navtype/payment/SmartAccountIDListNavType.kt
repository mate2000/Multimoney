package com.multimoney.multimoney.presentation.navigation.navtype.payment

import android.os.Bundle
import androidx.navigation.NavType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.multimoney.domain.model.accountsmart.SmartAccountID

class SmartAccountIDListNavType : NavType<List<SmartAccountID>>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): List<SmartAccountID>? {
        return bundle.getParcelableArrayList(key)
    }

    override fun parseValue(value: String): List<SmartAccountID> {
        val listType = object : TypeToken<ArrayList<SmartAccountID?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    override fun put(bundle: Bundle, key: String, value: List<SmartAccountID>) {
        bundle.putParcelableArray(key, value.toTypedArray())
    }
}
