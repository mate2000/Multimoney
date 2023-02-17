package com.multimoney.domain.model.accountsmart

import android.os.Parcelable
import com.multimoney.domain.model.balance.Account
import com.multimoney.domain.model.balance.Balance
import kotlinx.parcelize.Parcelize

@Parcelize
data class SmartAccountSmall(
    val totalBalance: Double?,
    val currencyCode: String?,
    val idCurrencyAccount: Int?,
    val accountToken: String = "",
    val accountNumber: String,
    val ibanAccountNumber: String
) : Parcelable

fun Account?.toSmartAccountSmall(): SmartAccountSmall {
    return SmartAccountSmall(
        totalBalance = this?.totalBalance,
        currencyCode = this?.currencyCode,
        idCurrencyAccount = this?.idCurrencyAccount,
        accountToken = this?.tokenNumber ?: "",
        accountNumber = this?.accountNumber ?: "",
        ibanAccountNumber = this?.ibanAccountNumber ?: ""
    )
}
