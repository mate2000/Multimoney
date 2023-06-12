package com.multimoney.multimoney.presentation.navigation.navtype.payment

import android.os.Bundle
import androidx.navigation.NavType
import com.google.gson.Gson
import com.multimoney.domain.model.security.InfoUser

class InfoUserNavType : NavType<InfoUser>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): InfoUser? {
        return bundle.getParcelable(key)
    }

    override fun parseValue(value: String): InfoUser {
        return Gson().fromJson(value, InfoUser::class.java)
    }

    override fun put(bundle: Bundle, key: String, value: InfoUser) {
        bundle.putParcelable(key, value)
    }
}