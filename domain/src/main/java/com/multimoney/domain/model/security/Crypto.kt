package com.multimoney.domain.model.security

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Crypto(
    val active: Boolean?,
    val origin: String?,
    val isTransferEnabled: Boolean?
) : Parcelable
