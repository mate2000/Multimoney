package com.multimoney.data.util.catalog

sealed class Nationalities(val name: String, val country: String, val documentSize: Int, val documentType: String) {
    object ElSalvador : Nationalities("Salvadoreña","El Salvador", 9, "DUI")
    object Guatemala : Nationalities("Guatemalteca","Guatemala", 13, "DPI")
    object CostaRicaDimex : Nationalities("Costarricense","Costa Rica", 12, "Dimex")
    object CostaRicaId : Nationalities("Costarricense","Costa Rica", 9, "Cédula")
}