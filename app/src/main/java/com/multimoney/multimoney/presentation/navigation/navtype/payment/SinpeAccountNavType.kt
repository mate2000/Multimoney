package com.multimoney.multimoney.presentation.navigation.navtype.payment

import android.os.Bundle
import androidx.navigation.NavType
import com.google.gson.Gson
import com.multimoney.domain.model.accountsmart.IbanAccountID

class SinpeAccountNavType : NavType<IbanAccountID>(isNullableAllowed = false) {

    override fun get(bundle: Bundle, key: String): IbanAccountID? {
        return bundle.getParcelable(key)
    }

    override fun parseValue(value: String): IbanAccountID {
        return Gson().fromJson(value, IbanAccountID::class.java)
    }

    override fun put(bundle: Bundle, key: String, value: IbanAccountID) {
        bundle.putParcelable(key, value)
    }
}
