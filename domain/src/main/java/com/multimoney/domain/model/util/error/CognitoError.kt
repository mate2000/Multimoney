package com.multimoney.domain.model.util.error

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CognitoError(
    val code: String,
    val message: String,
    val detail: String,
) : Parcelable