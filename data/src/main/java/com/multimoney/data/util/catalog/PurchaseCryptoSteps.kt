package com.multimoney.data.util.catalog

sealed class PurchaseCryptoSteps(
    val id: Int,
    val name: String
) {
    object One : PurchaseCryptoSteps(1, "List_Of_Currency")
    object Two : PurchaseCryptoSteps(2, "Select_Bank_Account")
    object Three : PurchaseCryptoSteps(3, "Buy_Crypto")
    object Four : PurchaseCryptoSteps(4, "Voucher")

    object Search {
        fun getIdByName(name: String?) = when (name) {
            One.name -> One.id
            Two.name -> Two.id
            Three.name -> Three.id
            Four.name -> Four.id
            else -> One.id
        }

        fun getNameById(id: Int) = when (id) {
            One.id -> One.name
            Two.id -> Two.name
            Three.id -> Three.name
            Four.id -> Four.name
            else -> One.name
        }
    }
}
