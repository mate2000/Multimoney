package com.multimoney.domain.model.credit

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ClientBankAccount(
    val id: Int?,
    val idBank: Int?,
    val origin: String?,
    val debitDate: String? = null,
    val bankDescription: String?,
    val accountNumber: String?,
    val idCurrency: Int?,
    val idCurrencyDestination: Int?
) : Parcelable
