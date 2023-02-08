package com.multimoney.multimoney.presentation.util.catalog

sealed class SignDocumentOrigin(val value: String) {
    object Product : SignDocumentOrigin("Product")
    object OnFidoFirstTime : SignDocumentOrigin("onFidoFirstTime")
    object OnFidoSecondTime : SignDocumentOrigin("onFidoSecondTime")
    object Crosseling : SignDocumentOrigin("Crosseling")
}
