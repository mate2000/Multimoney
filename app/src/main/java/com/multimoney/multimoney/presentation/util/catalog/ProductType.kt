package com.multimoney.multimoney.presentation.util.catalog

sealed class ProductType(val value: String) {
    object Credit : ProductType("Credit")
    object Smart : ProductType("Smart")
    object Crypto : ProductType("Crypto")
}
