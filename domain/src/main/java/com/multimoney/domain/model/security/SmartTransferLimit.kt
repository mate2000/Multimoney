package com.multimoney.domain.model.security

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SmartTransferLimit(
    val code: String,
    val description: String,
    val amount: Double?
) : Parcelable
