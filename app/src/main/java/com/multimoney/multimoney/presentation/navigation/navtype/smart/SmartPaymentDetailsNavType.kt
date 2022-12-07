package com.multimoney.multimoney.presentation.navigation.navtype.smart

import android.os.Bundle
import androidx.navigation.NavType
import com.google.gson.Gson
import com.multimoney.domain.model.accountsmart.navigation.SmartPaymentDetails

class SmartPaymentDetailsNavType : NavType<SmartPaymentDetails>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): SmartPaymentDetails? {
        return bundle.getParcelable(key)
    }

    override fun parseValue(value: String): SmartPaymentDetails {
        return Gson().fromJson(value, SmartPaymentDetails::class.java)
    }

    override fun put(bundle: Bundle, key: String, value: SmartPaymentDetails) {
        bundle.putParcelable(key, value)
    }
}