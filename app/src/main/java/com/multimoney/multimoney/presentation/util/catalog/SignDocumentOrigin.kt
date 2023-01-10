package com.multimoney.multimoney.presentation.util.catalog

sealed class SignDocumentOrigin(val value: String) {
    object Product : SignDocumentOrigin("Product")
    object OnFido : SignDocumentOrigin("onFido")
}