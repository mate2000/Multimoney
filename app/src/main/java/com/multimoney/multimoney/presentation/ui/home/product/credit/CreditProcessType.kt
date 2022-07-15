package com.multimoney.multimoney.presentation.ui.home.product.credit

enum class CreditProcessType(val type: String) {
    CREDIT_ACCEPT_CONTRACT_REFUSE_FIRST_TIME("credit_accept_contract_refuse_first_time"),
    CREDIT_START_PROCESS_INCOMPLETE("credit_start_process_incomplete"),
    CREDIT_ACCEPT_CONTRACT_REFUSE_SECOND_TIME("credit_accept_contract_refuse_second_time"),
    CREDIT_PROCESS_MISSING_SIGNATURE("credit_process_missing_signature"),
    CREDIT_PROCESS_SIGNATURE_REFUSE_FIRST_TIME("credit_process_signature_refuse_first_time"),
    CREDIT_PROCESS_SIGNATURE_REFUSE_SECOND_TIME("credit_accept_contract_refuse_second_time"),
    CREDIT_PROCESS_CREATE_ACCOUNT_FAILURE("credit_process_create_account_failure"),
}