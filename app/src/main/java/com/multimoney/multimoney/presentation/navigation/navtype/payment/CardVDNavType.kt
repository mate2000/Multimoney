package com.multimoney.multimoney.presentation.navigation.navtype.payment

import android.os.Bundle
import androidx.navigation.NavType
import com.google.gson.Gson
import com.multimoney.domain.model.credit.CardVisaDirect

class CardVDNavType : NavType<CardVisaDirect>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): CardVisaDirect? {
        return bundle.getParcelable(key)
    }

    override fun parseValue(value: String): CardVisaDirect {
        return Gson().fromJson(value, CardVisaDirect::class.java)
    }

    override fun put(bundle: Bundle, key: String, value: CardVisaDirect) {
        bundle.putParcelable(key, value)
    }
}
