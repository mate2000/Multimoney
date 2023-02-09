package com.multimoney.data.util

import com.google.gson.Gson
import java.lang.reflect.Type
import javax.inject.Inject

class GsonHelper @Inject constructor() {
    val gson by lazy { Gson() }

    fun convertToString(data: Any): String = gson.toJson(data)

    fun <R : Any> convertToData(json: String, dataClass: Class<R>): R =
        gson.fromJson(json, dataClass)

    inline fun <reified R> convertToListData(json: String, type: Type): List<R> =
        gson.fromJson(json, type)
}
