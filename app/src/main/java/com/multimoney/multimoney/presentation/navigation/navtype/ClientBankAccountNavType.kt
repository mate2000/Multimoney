package com.multimoney.multimoney.presentation.navigation.navtype

import android.os.Bundle
import androidx.navigation.NavType
import com.google.gson.Gson
import com.multimoney.domain.model.credit.ClientBankAccount

class ClientBankAccountNavType : NavType<ClientBankAccount>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): ClientBankAccount? {
        return bundle.getParcelable(key)
    }

    override fun parseValue(value: String): ClientBankAccount {
        return Gson().fromJson(value, ClientBankAccount::class.java)
    }

    override fun put(bundle: Bundle, key: String, value: ClientBankAccount) {
        bundle.putParcelable(key, value)
    }
}
