package com.multimoney.multimoney.presentation.navigation.navtype.payment

import android.os.Bundle
import androidx.navigation.NavType
import com.google.gson.Gson
import com.multimoney.domain.model.balance.BalanceCardInformation

class BalanceCardInformationNavType : NavType<BalanceCardInformation>(isNullableAllowed = false) {

    override fun get(bundle: Bundle, key: String): BalanceCardInformation? {
        return bundle.getParcelable(key)
    }

    override fun parseValue(value: String): BalanceCardInformation {
        return Gson().fromJson(value, BalanceCardInformation::class.java)
    }

    override fun put(bundle: Bundle, key: String, value: BalanceCardInformation) {
        bundle.putParcelable(key, value)
    }
}
