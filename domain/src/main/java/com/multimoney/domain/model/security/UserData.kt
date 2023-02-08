package com.multimoney.domain.model.security

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserData(
    var pkUser: String? = null,
    var userName: String? = null,
    var email: String? = null,
    var phoneNumber: String? = null,
    var fullName: String? = null,
    var firstName: String? = null,
    var secondName: String? = null,
    var firstLastName: String? = null,
    var secondLastName: String? = null,
    var nationality: String? = null,
    var identification: String? = null,
    val strIdIdentification: String? = null,
    val idIdentification: Int? = null,
    var countryCode: String? = null,
    var currentStep: String? = null,
    var userStatus: String? = null,
    var isNewUser: Boolean? = null,
    var maskedMail: String? = null,
    var maskedPhoneNumber: String? = null,
    var message: String? = null,
    var status: Int? = null,
    var detail: String? = null,
    var idBrand: Int? = 0
) : Parcelable
