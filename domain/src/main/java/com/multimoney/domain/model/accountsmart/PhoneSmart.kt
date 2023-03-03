package com.multimoney.domain.model.accountsmart

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PhoneSmart(
    val number: String?,
    val titular: String?,
    val bankName: String?,
    val identification: String?,
    val accountNumber: String?,
    val email: String?,
    val idCurrency: String?,
    val currency: String?,
    val ibanNumber: String?,
    val isFavorite: Boolean = false
) : Parcelable
