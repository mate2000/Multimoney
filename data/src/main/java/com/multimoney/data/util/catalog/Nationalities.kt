package com.multimoney.data.util.catalog

sealed class Nationalities(val name: String, val country: String, val documentSize: Int, val documentType: String) {
    object ElSalvadorDui : Nationalities("salvador","El Salvador", 9, "DUI")
    object ElSalvadorCarne : Nationalities("salvador","El Salvador", 7, "Carné")
    object Guatemala : Nationalities("guatemala","Guatemala", 13, "DPI")
    object CostaRicaDimex : Nationalities("costarica","Costa Rica", 12, "Dimex")
    object CostaRicaId : Nationalities("costarica","Costa Rica", 9, "Cédula")
}