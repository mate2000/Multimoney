package com.multimoney.data.util.catalog

sealed class UserStatus(val status: Int) {
    object Active : UserStatus(2701)
    object Blocked : UserStatus(1000)
    object Incomplete : UserStatus(0)
}
