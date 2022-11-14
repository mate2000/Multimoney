package com.multimoney.domain.model.accountsmart

/**
 * This object represent the data to be sent to the API, data will be collected
 * on every step from the smart flow.
 */
data class AccountSmartData(
    var pkUser: String? = "",
    var status: Int? = 0,
    var idProfessionType: Int? = 0,
    var idCivilStatusType: Long? = 0,
    var birthday: String = "",
    var expirationDate: String? = "",
    var idGender: Long? = 0,
    var idAddressLevel1: Long? = 0,
    var idAddressLevel2: Long? = 0,
    var idAddressLevel3: Long? = 0,
    var positionJob: String? = "",
    var idEconomicActivity: Long? = 0,
    var institutionPension: String = "",
    var income: Float? = 0.0f,
    var addressDetail: String? = "",
    var user: String? = "",
    var idBrand: Int? = 0,
    val currentStep: String? = "",
    val aboutCompany: String? = "",
    val companyName: String? = "",
    var specifiesIncomeSource: String? = "",
    var listBeneficiaries: List<Beneficiary>? = listOf(),
    var entrepreneurship: String = "",
    var legalID: String? = "",
    var isPEP: Boolean? = false,
    var isUSCitizen: Boolean? = null,
    var isActivityOfArt15: Boolean? = null,
    var isUSTaxPayer: Boolean? = null,
    var isTaxPayer: Boolean? = null
)
