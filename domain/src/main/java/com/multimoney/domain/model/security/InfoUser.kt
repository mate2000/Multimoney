package com.multimoney.domain.model.security

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class InfoUser(
    val idBrand: Int,
    val email: String,
    val userName: String,
    val idClient: Int,
    val firstName: String,
    val secondName: String,
    val lastName: String,
    val secondLastName: String,
    val phone: String,
    val countryCode: String,
    val visaDirectId: String?,
    val visaDirectUser: String?,
    val statusOnfido: String?
) : Parcelable
