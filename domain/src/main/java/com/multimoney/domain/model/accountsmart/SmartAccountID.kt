package com.multimoney.domain.model.accountsmart

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SmartAccountID(
    val tokenAccount: String?,
    val currencyID: Int?,
    val accountNumber: String?
) : Parcelable
