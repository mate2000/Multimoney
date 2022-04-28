package com.multimoney.multimoney.presentation.ui.personal

sealed class Nationalities(val country: String, val documentSize: Int) {
    object ElSalvador : Nationalities("El Salvador", 9)
    object Guatemala : Nationalities("Guatemala", 13)
    object CostaRicaDimex : Nationalities("Costa Rica", 12)
    object CostaRicaId : Nationalities("Costa Rica", 9)
}