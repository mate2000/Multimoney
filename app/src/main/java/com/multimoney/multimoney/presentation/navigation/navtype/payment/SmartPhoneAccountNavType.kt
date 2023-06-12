package com.multimoney.multimoney.presentation.navigation.navtype.payment

import android.os.Bundle
import androidx.navigation.NavType
import com.google.gson.Gson
import com.multimoney.domain.model.accountsmart.PhoneSmart

class SmartPhoneAccountNavType : NavType<PhoneSmart>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): PhoneSmart? {
        return bundle.getParcelable(key)
    }

    override fun parseValue(value: String): PhoneSmart {
        return Gson().fromJson(value, PhoneSmart::class.java)
    }

    override fun put(bundle: Bundle, key: String, value: PhoneSmart) {
        bundle.putParcelable(key, value)
    }
}
