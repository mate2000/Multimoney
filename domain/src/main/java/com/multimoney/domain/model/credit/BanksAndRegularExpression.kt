package com.multimoney.domain.model.credit

data class BanksAndRegularExpression(
    val banks: List<CreditCatalog>,
    val regularExpression: List<RegularExpression>
)