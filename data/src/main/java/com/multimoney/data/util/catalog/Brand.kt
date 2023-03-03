package com.multimoney.data.util.catalog

sealed class Brand(val id: Int, val iban: String, val countryCode: String, val phoneCode: String) {
    object CostaRica : Brand(5, "CR", "cr", "+506")
    object ElSalvador : Brand(7, "", "sv", "+503")
    object Guatemala : Brand(10, "", "gt", "+502")
    object Default : Brand(0, "", "", "")

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
