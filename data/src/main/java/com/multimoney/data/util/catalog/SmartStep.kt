package com.multimoney.data.util.catalog

sealed class SmartStep(val id: Int, val name: String) {
    object One : SmartStep(1, "Origination_Step1")
    object Two : SmartStep(2, "Origination_Step2")
    object Three : SmartStep(3, "Origination_Step3")
    object Four : SmartStep(4, "Origination_Step4")
    object Five : SmartStep(5, "Origination_Step5")
    object Six : SmartStep(6, "Origination_Step6")
    object Seven : SmartStep(7, "Origination_Step7")
    object Eight : SmartStep(8, "Origination_Step8")

    object Search {
        fun getIdByName(name: String?) = when (name) {
            SmartStep.One.name -> SmartStep.One.id
            SmartStep.Two.name -> SmartStep.Two.id
            SmartStep.Three.name -> SmartStep.Three.id
            SmartStep.Four.name -> SmartStep.Four.id
            SmartStep.Five.name -> SmartStep.Five.id
            SmartStep.Six.name -> SmartStep.Six.id
            SmartStep.Seven.name -> SmartStep.Seven.id
            SmartStep.Eight.name -> SmartStep.Eight.id
            else -> SmartStep.One.id
        }

        fun getNameById(id: Int) = when (id) {
            SmartStep.One.id -> SmartStep.One.name
            SmartStep.Two.id -> SmartStep.Two.name
            SmartStep.Three.id -> SmartStep.Three.name
            SmartStep.Four.id -> SmartStep.Four.name
            SmartStep.Five.id -> SmartStep.Five.name
            SmartStep.Six.id -> SmartStep.Six.name
            SmartStep.Seven.id -> SmartStep.Seven.name
            SmartStep.Eight.id -> SmartStep.Eight.name
            else -> SmartStep.One.name
        }
    }
}
