package com.multimoney.multimoney.presentation.navigation.navtype.payment

import android.os.Bundle
import androidx.navigation.NavType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.multimoney.domain.model.accountsmart.SinpeAccount
import com.multimoney.domain.model.accountsmart.SmartAccountID

class SinpeAccountNavType : NavType<SinpeAccount>(isNullableAllowed = false) {

    override fun get(bundle: Bundle, key: String): SinpeAccount? {
        return bundle.getParcelable(key)
    }

    override fun parseValue(value: String): SinpeAccount {
        return Gson().fromJson(value, SinpeAccount::class.java)
    }

    override fun put(bundle: Bundle, key: String, value: SinpeAccount) {
        bundle.putParcelable(key, value)
    }
}