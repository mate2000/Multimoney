package com.multimoney.domain.model.credit

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ClientBankAccount(
    val id: Int? = null,
    val idBank: Int? = null,
    val origin: String? = null,
    val debitDate: String? = null,
    val bankDescription: String? = null,
    val accountNumber: String? = null,
    val idCurrency: Int? = null,
    val idCurrencyDestination: Int? = null
) : Parcelable
