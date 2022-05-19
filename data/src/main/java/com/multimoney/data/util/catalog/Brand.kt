package com.multimoney.data.util.catalog

sealed class Brand(val id: Int) {
    object Revamp : Brand(5)
}