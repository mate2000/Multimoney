package com.multimoney.data.util.catalog

sealed class CreditStep(val id: Int, val name: String) {
    object One : CreditStep(1, "Origination_Step1")
    object Two : CreditStep(2, "Origination_Step2")
    object Three : CreditStep(3, "Origination_Step3")
    object Four : CreditStep(4, "Origination_Step4")
    object Five : CreditStep(5, "Origination_Step5")
    object Six : CreditStep(6, "Origination_Step6")
    object Seven : CreditStep(7, "Origination_Step7")

    object Search {
        fun getIdByName(name: String?) = when (name) {
            CreditStep.One.name -> CreditStep.One.id
            CreditStep.Two.name -> CreditStep.Two.id
            CreditStep.Three.name -> CreditStep.Three.id
            CreditStep.Four.name -> CreditStep.Four.id
            CreditStep.Five.name -> CreditStep.Five.id
            CreditStep.Six.name -> CreditStep.Six.id
            CreditStep.Seven.name -> CreditStep.Seven.id
            else -> CreditStep.One.id
        }

        fun getNameById(id: Int) = when (id) {
            CreditStep.One.id -> CreditStep.One.name
            CreditStep.Two.id -> CreditStep.Two.name
            CreditStep.Three.id -> CreditStep.Three.name
            CreditStep.Four.id -> CreditStep.Four.name
            CreditStep.Five.id -> CreditStep.Five.name
            CreditStep.Six.id -> CreditStep.Six.name
            CreditStep.Seven.id -> CreditStep.Seven.name
            else -> CreditStep.One.name
        }
    }
}
