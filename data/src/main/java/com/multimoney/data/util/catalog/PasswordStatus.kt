package com.multimoney.data.util.catalog

sealed class PasswordStatus(val status: Int) {
    object PasswordInvalid : PasswordStatus(0)
    object PasswordSaved : PasswordStatus(1)
}