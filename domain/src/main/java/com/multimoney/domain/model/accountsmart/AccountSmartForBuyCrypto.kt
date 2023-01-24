package com.multimoney.domain.model.accountsmart

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AccountSmartForBuyCrypto(
    val totalBalance: Double?,
    val currencyCode: String?,
    val accountNumber: String?,
    val ibanAccountNumber: String?,
    val idCurrencyAccount: Int?
) : Parcelable
