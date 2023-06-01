package com.multimoney.data.util.catalog

enum class AccountStatus(val status: Int) {
    PARTIAL_LOCK(3),
    FULL_LOOK(4),
    EMBARGO(5),
    JUDICIAL_RECOVERY(7),
    PRESCRIBED(8)
}