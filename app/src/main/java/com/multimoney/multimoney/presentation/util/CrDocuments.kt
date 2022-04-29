package com.multimoney.multimoney.presentation.util

sealed class CrDocuments(val document: String) {
    object IdDocument : CrDocuments("Cédula")
    object Dimex : CrDocuments("Dimex")
}