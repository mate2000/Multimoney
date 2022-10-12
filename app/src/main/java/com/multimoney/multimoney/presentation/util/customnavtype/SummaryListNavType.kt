package com.multimoney.multimoney.presentation.util.customnavtype

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.multimoney.domain.model.balance.Summary

class SummaryListNavType : NavType<List<Summary>>(isNullableAllowed = false) {

    override fun get(bundle: Bundle, key: String): List<Summary>? {
        return bundle.getParcelableArrayList(key)
    }

    override fun parseValue(value: String): List<Summary> {
        val listType = object : TypeToken<ArrayList<Summary?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    override fun put(bundle: Bundle, key: String, value: List<Summary>) {
        bundle.putParcelableArray(key, value.toTypedArray())
    }
}

fun encodeData(list: List<Summary>?): String {
    val listType = object : TypeToken<ArrayList<Summary?>?>() {}.type
    return Uri.encode(Gson().toJson(list, listType))
}