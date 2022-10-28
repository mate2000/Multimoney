package com.multimoney.domain.model.security

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Credit(
    val active: Boolean?,
    val paymentMethod: List<PaymentMethod?>?,
    val transferAccount: TransferAccount?
) : Parcelable
