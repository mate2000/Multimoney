package com.multimoney.domain.model.security

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Configuration(
    val timeSession: Int?,
    val currency: List<Currency?>?,
    val accountSmart: AccountSmart?,
    val credit: Credit?,
    val crypto: Crypto?,
    val virtualCard: VirtualCard?
) : Parcelable
