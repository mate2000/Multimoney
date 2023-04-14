package com.multimoney.domain.model.util.parametercorelog

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EnrollDeviceParameters(
    val identification: String,
    val phone:String
) : Parcelable

