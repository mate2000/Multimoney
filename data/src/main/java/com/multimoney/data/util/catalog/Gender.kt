package com.multimoney.data.util.catalog

sealed class Gender(val id: Int, val gender: String){
    object Female : SignUpStep(1, "Femenino")
    object Male : SignUpStep(2, "Masculino")
}