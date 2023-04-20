package com.multimoney.domain.model.util.parametercorelog

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VisaDirectIncludeCardParameters(
    val applicationName: String?,
    val userName: String?,
    val userPassword: String?,
    val endpoint: String?
) : Parcelable