package com.multimoney.data.util.catalog

import com.multimoney.data.util.catalog.PurchaseCryptoSteps.ListOfCryptoCurrency.direct

sealed class PurchaseCryptoSteps(
    val id: Int? = null,
) {
    data class ListOfCryptoCurrency(
        val direct: Boolean,
        val comingFromDetails: Boolean
    ) : PurchaseCryptoSteps(1) {
        fun getDirectId() = if (direct) null else 1
        fun getComingFromDetailsId() = if (comingFromDetails) null else 1
    }

    data class SelectBankAccount(
        val direct: Boolean,
        val comingFromDetails: Boolean
    ) : PurchaseCryptoSteps(2) {
        fun getComingFromDetailsId() = if (comingFromDetails) 1 else 2
        fun getDirectId() = if (direct) 1 else 2
    }
    object BuyCryptoCurrency : PurchaseCryptoSteps(3)
    object ConfirmPurchase : PurchaseCryptoSteps(4)
}
