package com.multimoney.domain.model.util.parametercorelog

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EnrollCardParameters(
    val userId: String?,
    val email: String?,
    val accountNumber: String?,
    val cardName: String,
    val cvv2: String,
    val month: String,
    val year: String
) : Parcelable
