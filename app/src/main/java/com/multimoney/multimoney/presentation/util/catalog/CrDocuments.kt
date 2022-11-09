package com.multimoney.multimoney.presentation.util.catalog

sealed class CrDocuments(val document: String) {
    object IdDocument : CrDocuments("Cédula")
    object Dimex : CrDocuments("Dimex")
}