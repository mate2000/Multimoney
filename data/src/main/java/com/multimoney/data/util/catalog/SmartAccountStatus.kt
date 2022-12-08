package com.multimoney.data.util.catalog

enum class SmartAccountStatus(val status: Int) {
    NO_EXIST(0),
    EXIST_IN_CORE(1),
    PENDING_PROCESS(2),
    APPROVED_SMART(3),
    SMART_ACTIVE(4),
    PENDING_APPROVE(5),
    WITHOUT_OPERATION(6),
   SMART_PRE_APPROVED(7),
    SMART_NOT_PRE_APPROVED(8),
    SMART_REJECTED(9),
}
