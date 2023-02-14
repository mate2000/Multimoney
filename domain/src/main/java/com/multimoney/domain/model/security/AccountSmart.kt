package com.multimoney.domain.model.security

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AccountSmart(
    val active: Boolean?,
    val paymentMethod: List<PaymentMethod?>?,
    val transferMethod: List<PaymentMethod?>?,
    val transferLimit: List<SmartTransferLimit?>?
) : Parcelable
