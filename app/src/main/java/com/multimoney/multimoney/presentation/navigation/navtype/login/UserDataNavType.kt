package com.multimoney.multimoney.presentation.navigation.navtype.login

import android.os.Bundle
import androidx.navigation.NavType
import com.google.gson.Gson
import com.multimoney.domain.model.security.UserData

class UserDataNavType : NavType<UserData>(isNullableAllowed = true) {

    override fun get(bundle: Bundle, key: String): UserData? {
        return bundle.getParcelable(key)
    }

    override fun parseValue(value: String): UserData {
        return Gson().fromJson(value, UserData::class.java)
    }

    override fun put(bundle: Bundle, key: String, value: UserData) {
        bundle.putParcelable(key, value)
    }
}
