package com.multimoney.data.util.catalog

sealed class Brand(val id: Int, val iban: String) {
    object CostaRica : Brand(5, "CR")
    object ElSalvador : Brand(7, "")
    object Guatemala : Brand(10, "")

    object Search {
        fun getIdBrandByNationality(nationality: String?): Int {
            return when (nationality) {
                Nationalities.ElSalvador.name.lowercase() -> ElSalvador.id
                Nationalities.Guatemala.name.lowercase() -> Guatemala.id
                Nationalities.CostaRicaId.name.lowercase() -> CostaRica.id
                else -> 0
            }
        }
    }
}
