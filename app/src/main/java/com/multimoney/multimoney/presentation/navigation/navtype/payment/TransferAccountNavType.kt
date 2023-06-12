package com.multimoney.multimoney.presentation.navigation.navtype.payment

import android.os.Bundle
import androidx.navigation.NavType
import com.google.gson.Gson
import com.multimoney.domain.model.security.TransferAccount

class TransferAccountNavType : NavType<TransferAccount>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): TransferAccount? {
        return bundle.getParcelable(key)
    }

    override fun parseValue(value: String): TransferAccount {
        return Gson().fromJson(value, TransferAccount::class.java)
    }

    override fun put(bundle: Bundle, key: String, value: TransferAccount) {
        bundle.putParcelable(key, value)
    }
}
