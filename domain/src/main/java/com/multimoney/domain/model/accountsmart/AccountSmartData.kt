package com.multimoney.domain.model.accountsmart

data class AccountSmartData(
    var pkUser: String? = "",
    var status: Int? = 0,
    var idProfessionType: Int? = 0,
    var idAddressLevel1: Long? = 0,
    var idAddressLevel2: Long? = 0,
    var idAddressLevel3: Long? = 0,
    var idEconomicActivity: Long? = 0,
    var income: Float? = 0.0f,
    var addressDetail: String? = "",
    var isPEP: Boolean? = false,
    var user: String? = "",
    var idBrand: Int? = 0,
    val currentStep: String? = "",
)
