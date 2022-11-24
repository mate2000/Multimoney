package com.multimoney.data.util.catalog

sealed class SmartSteps(val id: Int, val name: String) {
    object One : SmartSteps(1, "Paso_1")
    object Two : SmartSteps(2, "Paso_2")
    object Three : SmartSteps(3, "Paso_3")
    object Four : SmartSteps(4, "Paso_4")
    object Five : SmartSteps(5, "Paso_5")
    object Six : SmartSteps(6, "Paso_6")

    object Search {
        fun getIdByName(name: String?) = when (name) {
            One.name -> One.id
            Two.name -> Two.id
            Three.name -> Three.id
            Four.name -> Four.id
            Five.name -> Five.id
            Six.name -> Six.id
            else -> One.id
        }

        fun getNameById(id: Int) = when (id) {
            One.id -> One.name
            Two.id -> Two.name
            Three.id -> Three.name
            Four.id -> Four.name
            Five.id -> Five.name
            Six.id -> Six.name
            else -> One.name
        }
    }
}
