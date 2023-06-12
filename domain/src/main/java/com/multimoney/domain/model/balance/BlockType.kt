package com.multimoney.domain.model.balance

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BlockType(
    val code: String?,
    val msg: String?
) : Parcelable
