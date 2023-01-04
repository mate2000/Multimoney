package com.multimoney.multimoney.presentation.navigation.navtype.payment

import android.os.Bundle
import androidx.navigation.NavType
import com.google.gson.Gson
import com.multimoney.domain.model.accountsmart.SmartAccountID

class SmartAccountIDNavType : NavType<SmartAccountID>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): SmartAccountID? {
        return bundle.getParcelable(key)
    }

    override fun parseValue(value: String): SmartAccountID {
        return Gson().fromJson(value, SmartAccountID::class.java)
    }

    override fun put(bundle: Bundle, key: String, value: SmartAccountID) {
        bundle.putParcelable(key, value)
    }
}
