package com.multimoney.multimoney.presentation.navigation.navtype.payment

import android.os.Bundle
import androidx.navigation.NavType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.multimoney.domain.model.security.PaymentMethod

class PaymentMethodListNavType : NavType<List<PaymentMethod>>(isNullableAllowed = false) {

    override fun get(bundle: Bundle, key: String): List<PaymentMethod>? {
        return bundle.getParcelableArrayList(key)
    }

    override fun parseValue(value: String): List<PaymentMethod> {
        val listType = object : TypeToken<ArrayList<PaymentMethod?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    override fun put(bundle: Bundle, key: String, value: List<PaymentMethod>) {
        bundle.putParcelableArray(key, value.toTypedArray())
    }
}
