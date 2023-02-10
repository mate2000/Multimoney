package com.multimoney.domain.model.accountsmart

import android.os.Parcelable
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
