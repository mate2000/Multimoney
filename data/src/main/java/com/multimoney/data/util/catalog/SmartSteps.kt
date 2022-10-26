package com.multimoney.data.util.catalog

sealed class SmartSteps(val id: Int, val name: String) {
    object One : SmartStep(1, "Paso_1")
    object Two : SmartStep(2, "Paso_2")
    object Three : SmartStep(3, "Paso_3")
    object Four : SmartStep(4, "Paso_4")
    object Five : SmartStep(5, "Paso_5")
    object Six : SmartStep(6, "Paso_6")
    object Seven : SmartStep(7, "Paso_7")
    object Eight : SmartStep(8, "Paso_8")

    object Search {
        fun getIdByName(name: String?) = when (name) {
            One.name -> One.id
            Two.name -> Two.id
            Three.name -> Three.id
            Four.name -> Four.id
            Five.name -> Five.id
            Six.name -> Six.id
            Seven.name -> Seven.id
            Eight.name -> Eight.id
            else -> One.id
        }

        fun getNameById(id: Int) = when (id) {
            One.id -> One.name
            Two.id -> Two.name
            Three.id -> Three.name
            Four.id -> Four.name
            Five.id -> Five.name
            Six.id -> Six.name
            Seven.id -> Seven.name
            Eight.id -> Eight.name
            else -> One.name
        }
    }
}