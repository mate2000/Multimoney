package com.multimoney.multimoney.presentation.ui.personal

sealed class CrDocuments(val document: String) {
    object IdDocument : CrDocuments("Cédula")
    object Dimex : CrDocuments("Dimex")
}