package com.multimoney.domain.model.security

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VirtualCard(
    val active: Boolean?
) : Parcelable
