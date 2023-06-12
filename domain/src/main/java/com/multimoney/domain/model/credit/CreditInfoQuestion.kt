package com.multimoney.domain.model.credit

data class CreditInfoQuestion(
    val idQuestionRequestCredit: Int?,
    val idOptionQuestionRequestCredit: Int?,
    val createUser: String?,
    val updateUser: String?,
    val identificator: String?,
    val value: String,
    val active: Boolean = true,
    val controlType: String?,
    val isCoreCatalogue: Boolean?,
    val isBranchOfficeCatalogue: Boolean?,
    val useValue: Boolean?,
    val maximumAmount: String?,
    val description: String?,
    val valueCatalogue: String?,
    val idIdentificatorCatalogue: String?
)
