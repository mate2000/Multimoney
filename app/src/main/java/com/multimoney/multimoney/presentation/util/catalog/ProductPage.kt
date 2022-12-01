package com.multimoney.multimoney.presentation.util.catalog

data class ProductPage(
    val product: String,
    val index: Int,
    val resourceText: Int,
    val resourceIcon: Int,
    val productSmartIndex: Int = 0,
    val enabled: Boolean
)
