package com.multimoney.data.util.catalog

sealed class SignUpStep(val id: Int, val name: String) {
    object One : SignUpStep(1, "Register_Step1")
    object Two : SignUpStep(2, "Register_Step2")
    object Three : SignUpStep(3, "Register_Step3")
    object Four : SignUpStep(4, "Register_Step4")
    object Five : SignUpStep(5, "Register_Step5")
    object Six : SignUpStep(6, "Register_Step6")
}