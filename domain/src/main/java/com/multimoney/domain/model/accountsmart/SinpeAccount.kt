package com.multimoney.domain.model.accountsmart

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SinpeAccount(
    val accountId: Int,
    val country: String,
    val bank: String,
    val clientIdentification: String,
    val sinpeAccount: String,
    val active: Boolean,
    val currencyId: Int,
    val currency: String,
    val nameAccount: String
) : Parcelable
