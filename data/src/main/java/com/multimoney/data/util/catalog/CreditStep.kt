package com.multimoney.data.util.catalog

sealed class CreditStep(val id: Int, val name: String) {
    object One : CreditStep(1, "Credit_Step1")
    object Two : CreditStep(2, "Credit_Step2")
    object Three : CreditStep(3, "Credit_Step3")
    object Four : CreditStep(4, "Credit_Step4")
    object Five : CreditStep(5, "Credit_Step5")
    object Six : CreditStep(6, "Credit_Step6")

    object Search {
        fun getIdByName(name: String?) = when (name) {
            CreditStep.One.name -> CreditStep.One.id
            CreditStep.Two.name -> CreditStep.Two.id
            CreditStep.Three.name -> CreditStep.Three.id
            CreditStep.Four.name -> CreditStep.Four.id
            CreditStep.Five.name -> CreditStep.Five.id
            else -> CreditStep.Six.id
        }

        fun getNameById(id: Int) = when (id) {
            CreditStep.One.id -> CreditStep.One.name
            CreditStep.Two.id -> CreditStep.Two.name
            CreditStep.Three.id -> CreditStep.Three.name
            CreditStep.Four.id -> CreditStep.Four.name
            CreditStep.Five.id -> CreditStep.Five.name
            else -> CreditStep.Six.name
        }
    }
}
