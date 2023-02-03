package com.multimoney.multimoney.presentation.navigation.navtype.payment

import android.os.Bundle
import androidx.navigation.NavType
import com.google.gson.Gson
import com.multimoney.domain.model.accountsmart.Transfer365Account

class Transfer365AccountNavType : NavType<Transfer365Account>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): Transfer365Account? {
        return bundle.getParcelable(key)
    }

    override fun parseValue(value: String): Transfer365Account {
        return Gson().fromJson(value, Transfer365Account::class.java)
    }

    override fun put(bundle: Bundle, key: String, value: Transfer365Account) {
        bundle.putParcelable(key, value)
    }
}