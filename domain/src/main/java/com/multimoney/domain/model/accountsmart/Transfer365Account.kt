package com.multimoney.domain.model.accountsmart

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Transfer365Account(
    val accountNumber: String? = null,
    val phone: String? = null,
    val identification: String = "",
    val name: String = "",
    val lastname: String = "",
    val bankId: String = "",
    val bankName: String = "",
    val accountTypeId: String = "",
    val destinationType: String? = null,
    val isFavorite: Boolean = false
) : Parcelable
