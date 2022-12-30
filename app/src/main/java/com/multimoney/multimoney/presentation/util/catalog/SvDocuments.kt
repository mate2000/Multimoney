package com.multimoney.multimoney.presentation.util.catalog

sealed class SvDocuments(val document: String) {
    object DuiDocument : SvDocuments("DUI")
    object CarneDocument : SvDocuments("Carné")
}