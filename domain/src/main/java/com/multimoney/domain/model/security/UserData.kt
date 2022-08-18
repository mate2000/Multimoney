package com.multimoney.domain.model.security

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
    var identificationValueType: String? = null,
    var identification: String? = null,
    var countryCode: String? = null,
    var currentStep: String? = null,
    var userStatus: String? = null,
    var idBrand: Int? = null
)