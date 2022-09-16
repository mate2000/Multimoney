package com.multimoney.data.util.catalog

enum class CreditStatus(val status: Int) {
    NO_EXIST(0),
    EXIST_IN_CORE(1),
    PENDING_PROCESS(2),
    APPROVED_CREDIT(3),
    CREDIT_ACTIVE(4),
    PENDING_APROVE(5),
    WHITOUT_OPERATION(6),
    CREDIT_PRE_APPROVED(7),
    CREDIT_NOT_PRE_APPROVED(8),
    UNAPPROVED_CREDIT(9),
}