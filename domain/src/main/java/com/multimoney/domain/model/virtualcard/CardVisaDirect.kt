package com.multimoney.domain.model.virtualcard

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CardVisaDirect(
    val idCard: Int? = null,
    val creationDate: String? = null,
    val verified: Boolean? = null,
    val detail: String? = null,
    val cardMaskedNumber: String? = null,
    val expirationMonth: String? = null,
    val expirationYear: String? = null,
    val cardTokenId: String? = null,
    val currencyDescription: String? = null,
    val country: String? = null,
    val debitDate: String? = ""
) : Parcelable
