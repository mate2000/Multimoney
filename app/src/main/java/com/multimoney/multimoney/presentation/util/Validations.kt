package com.multimoney.multimoney.presentation.util

import android.util.Patterns

fun isEmailValid(email: String?): Boolean {
    return email?.let {
        it.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(it).matches()
    } ?: false
}