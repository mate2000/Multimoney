package com.multimoney.multimoney.presentation.navigation.navtype.payment

import android.os.Bundle
import androidx.navigation.NavType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.multimoney.domain.model.accountsmart.SinpeAccount

class SinpeAccountListNavType : NavType<List<SinpeAccount>>(isNullableAllowed = false) {

    override fun get(bundle: Bundle, key: String): List<SinpeAccount>? {
        return bundle.getParcelableArrayList(key)
    }

    override fun parseValue(value: String): List<SinpeAccount> {
        val listType = object : TypeToken<ArrayList<SinpeAccount?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    override fun put(bundle: Bundle, key: String, value: List<SinpeAccount>) {
        bundle.putParcelableArray(key, value.toTypedArray())
    }
}