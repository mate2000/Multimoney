package com.multimoney.multimoney.presentation.navigation.navtype.payment

import android.os.Bundle
import androidx.navigation.NavType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.multimoney.domain.model.accountsmart.SmartAccountSmall

class SmartAccountListNavType : NavType<List<SmartAccountSmall>>(isNullableAllowed = false) {

    override fun get(bundle: Bundle, key: String): List<SmartAccountSmall>? {
        return bundle.getParcelableArrayList(key)
    }

    override fun parseValue(value: String): List<SmartAccountSmall> {
        val listType = object : TypeToken<ArrayList<SmartAccountSmall?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    override fun put(bundle: Bundle, key: String, value: List<SmartAccountSmall>) {
        bundle.putParcelableArray(key, value.toTypedArray())
    }
}
