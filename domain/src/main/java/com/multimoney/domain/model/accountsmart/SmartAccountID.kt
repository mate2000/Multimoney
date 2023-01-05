package com.multimoney.domain.model.accountsmart

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SmartAccountID(
    val tokenAccount: String?,
    val currencyID: Int?,
    val accountNumber: String?,
    val ibanAccountNumber: String? = "",
    val totalBalance: Double? = null
) : Parcelable

@Parcelize
data class IbanAccountID(
    val bank: String? = "",
    val clientIdentification: String? = "",
    val sinpeAccount: String? = "",
    val currencyId: Int? = 0,
    val nameAccount: String? = "",
) : Parcelable
