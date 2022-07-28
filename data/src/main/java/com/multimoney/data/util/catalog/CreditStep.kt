package com.multimoney.data.util.catalog

sealed class CreditStep(val id: Int, val name: String) {
    object One : CreditStep(1, "Credit_Step1")
    object Two : CreditStep(2, "Credit_Step2")

    object Search {
        fun getIdByName(name: String?) = when (name) {
            CreditStep.One.name -> CreditStep.One.id
            else -> CreditStep.Two.id
        }
    }
}
