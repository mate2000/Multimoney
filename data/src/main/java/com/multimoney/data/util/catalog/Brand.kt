package com.multimoney.data.util.catalog

sealed class Brand(val id: Int, val iban: String, val countryCode: String, val phoneCode: String) {
    object CostaRica : Brand(5, "CR", "cr", "+506")
    object ElSalvador : Brand(7, "", "sv", "+503")
    object Guatemala : Brand(10, "", "gt", "+502")
    object Mexico : Brand(12, "", "mx", "+52")
    object Default : Brand(0, "", "", "")

    object Search {
        fun getIdBrandByCountryCode(countryCode: String): Int {
            return when (countryCode) {
                ElSalvador.countryCode.lowercase() -> ElSalvador.id
                Guatemala.countryCode.lowercase() -> Guatemala.id
                CostaRica.countryCode.lowercase() -> CostaRica.id
                Mexico.countryCode.lowercase() -> Mexico.id
                else -> CostaRica.id
            }
        }
    }
}
