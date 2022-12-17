package com.multimoney.multimoney.presentation.navigation.navtype.payment

import android.os.Bundle
import androidx.navigation.NavType
import com.google.gson.Gson
import com.multimoney.domain.model.balance.CardInformation

class CardInformationNavType : NavType<CardInformation>(isNullableAllowed = false) {

    override fun get(bundle: Bundle, key: String): CardInformation? {
        return bundle.getParcelable(key)
    }

    override fun parseValue(value: String): CardInformation {
        return Gson().fromJson(value, CardInformation::class.java)
    }

    override fun put(bundle: Bundle, key: String, value: CardInformation) {
        bundle.putParcelable(key, value)
    }
}
