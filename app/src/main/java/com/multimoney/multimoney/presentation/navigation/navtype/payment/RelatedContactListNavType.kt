package com.multimoney.multimoney.presentation.navigation.navtype.payment

import android.os.Bundle
import androidx.navigation.NavType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.multimoney.domain.model.accountsmart.RelatedContact

class RelatedContactListNavType : NavType<List<RelatedContact>>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): List<RelatedContact>? {
        return bundle.getParcelableArrayList(key)
    }

    override fun parseValue(value: String): List<RelatedContact> {
        val listType = object : TypeToken<ArrayList<RelatedContact?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    override fun put(bundle: Bundle, key: String, value: List<RelatedContact>) {
        bundle.putParcelableArray(key, value.toTypedArray())
    }
}