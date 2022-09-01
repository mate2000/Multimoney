package com.multimoney.domain.model.credit

data class HomeAddress(
    val province: List<Province?>?,
    val canton: List<Canton?>?,
    val district: List<District?>?
)