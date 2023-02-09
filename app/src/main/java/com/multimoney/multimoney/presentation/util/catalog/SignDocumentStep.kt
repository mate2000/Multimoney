package com.multimoney.multimoney.presentation.util.catalog

enum class SignDocumentStep(val value: String) {
    GENERATE_DOCUMENT_STEP("generate_document_step"),
    SIGN_DOCUMENTS_STEP("sign_documents_step"),
    VALIDATE_IDENTITY("validate_identity"),
    PROCESSING_TRANSACTION("processing_transaction")
}
