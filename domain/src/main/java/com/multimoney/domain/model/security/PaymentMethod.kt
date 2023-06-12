package com.multimoney.domain.model.security

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentMethod(
    val description: String?,
    val type: String?,
    val mask: String?,
    val active: Boolean?
) : Parcelable
