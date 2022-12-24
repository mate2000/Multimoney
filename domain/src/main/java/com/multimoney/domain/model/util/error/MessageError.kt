package com.multimoney.domain.model.util.error

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MessageError(
    val status: Int?,
    val message: String?,
    val detail: String?
) : Parcelable
