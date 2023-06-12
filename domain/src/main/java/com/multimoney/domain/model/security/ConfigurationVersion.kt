package com.multimoney.domain.model.security

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ConfigurationVersion(
    val active: Boolean?,
    val configuration: Configuration?
) : Parcelable
