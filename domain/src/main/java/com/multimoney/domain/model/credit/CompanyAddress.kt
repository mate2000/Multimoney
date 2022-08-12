package com.multimoney.domain.model.credit

data class CompanyAddress(
    val companyProvince: List<CompanyProvince?>?,
    val companyCanton: List<CompanyCanton?>?,
    val companyDistrict: List<CompanyDistrict?>?
)
