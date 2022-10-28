package com.multimoney.data.util.catalog

sealed class Gender(val id: Long, val gender: String) {
    object Female : SignUpStep(1, "Femenino")
    object Male : SignUpStep(2, "Masculino")

    object Search {
        fun getGenderList() = listOf(Female.name, Male.name)

        fun getGenderIdByName(name: String) =
            (if (Female.name == name) Female.id else Male.id).toLong()
    }
}