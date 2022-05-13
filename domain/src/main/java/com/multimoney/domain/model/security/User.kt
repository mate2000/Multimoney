package com.multimoney.domain.model.security

data class User(
    val pkUser: String?,
    val userName: String?,
    val email: String?,
    val phoneNumber: String?,
    val fullName: String?,
    val firstName: String?,
    val secondName: String?,
    val lastName: String?,
    val secondLastName: String?,
    val contactMeans: String?,
    val nationality: String?,
    val identification: String?,
    val countryCode: String?,
    val currentStep: String?,
    val userStatus: String?,
)