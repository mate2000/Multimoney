package com.multimoney.data.util.catalog

sealed class Brand(val id: Int) {
    object CostaRica : Brand(5)
    object ElSalvador : Brand(7)
    object Guatemala : Brand(10)
}