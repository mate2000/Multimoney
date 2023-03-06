package com.multimoney.domain.model.credit

data class Product(
    val id: String?,
    val creditLimit: String?,
    val progressFactor: Double?,
    val minimumDisbursement: String?,
    val minimumDisbursementLabel: String?,
    val maximumDisbursement: String?,
    val maximumDisbursementLabel: String?,
    val term: Int?,
    val termLabel: String?,
    val currency: String?,
    val regularInterestRate: String?,
    val regularInterestRateLabel: String?,
    val fee: Double?,
    val feeLabel: String?,
    val commissionDisbursement: String?,
    val commissionDisbursementLabel: String?,
    val paymentDate: String?,
    val currencyName: String?,
    val messageConditions: String?,
    val isFormalizationRequired: String?,
    val idPromotion: String?
)
