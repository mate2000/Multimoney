package com.multimoney.data.util.catalog

sealed class Brand(val id: Int, val iban: String, val countryCode: String) {
    object CostaRica : Brand(5, "CR", "cr")
    object ElSalvador : Brand(7, "", "sv")
    object Guatemala : Brand(10, "", "gt")
    object Default : Brand(0, "", "")

    object Search {
        fun getIdBrandByNationality(nationality: String?): Int {
            return when (nationality?.lowercase()) {
                Nationalities.ElSalvadorDui.country.lowercase() -> ElSalvador.id
                Nationalities.Guatemala.country.lowercase() -> Guatemala.id
                Nationalities.CostaRicaId.country.lowercase() -> CostaRica.id
                else -> 0
            }
        }
    }
}
