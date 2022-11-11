package com.multimoney.multimoney.presentation.util.catalog

enum class SignDocumentStep(val value: String) {
    GENERATE_DOCUMENT_STEP("generate_document_step"),
    SIGN_DOCUMENTS_STEP("sign_documents_step"),
    EVICERTIA_REJECTED_FIRST_TIME_STEP("evicertia_rejected_first_time_step"),
    EVICERTIA_REJECTED_SECOND_TIME_STEP("evicertia_rejected_second_time_step"),
    VALIDATE_IDENTITY("validate_identity"),
    ONFIDO_REJECTED_FIRST_TIME("onfido_rejected_first_time"),
    ONFIDO_REJECTED_SECOND_TIME("onfido_rejected_second_time"),
    CONTINUE_VALIDATING_IDENTITY("continue_validating_identity"),
    PROCESSING_THE_TRANSACTION("processing_the_transaction")
}
