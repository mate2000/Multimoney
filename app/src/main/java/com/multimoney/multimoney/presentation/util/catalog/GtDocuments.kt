package com.multimoney.multimoney.presentation.util.catalog

sealed class GtDocuments(val document: String) {
    object DPI : CrDocuments("DPI")
}