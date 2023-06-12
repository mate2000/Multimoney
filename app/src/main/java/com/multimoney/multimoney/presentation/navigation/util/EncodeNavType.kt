package com.multimoney.multimoney.presentation.navigation.util

import android.net.Uri
import android.os.Parcelable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun <T : Parcelable> encodeData(data: T?): String {
    return Uri.encode(Gson().toJson(data))
}

fun <T : Parcelable> encodeData(list: List<T?>?): String {
    val listType = object : TypeToken<ArrayList<T>?>() {}.type
    return Uri.encode(Gson().toJson(list, listType))
}
