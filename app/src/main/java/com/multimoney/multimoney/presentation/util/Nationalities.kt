package com.multimoney.multimoney.presentation.util

sealed class Nationalities(val name: String, val country: String, val documentSize: Int) {
    object ElSalvador : Nationalities("Salvadoreña","El Salvador", 9)
    object Guatemala : Nationalities("Guatemalteca","Guatemala", 13)
    object CostaRicaDimex : Nationalities("Costarricense","Costa Rica", 12)
    object CostaRicaId : Nationalities("Costarricense","Costa Rica", 9)
}