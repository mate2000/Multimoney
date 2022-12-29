package com.multimoney.data.mapper.credit

import com.multimoney.data.networking.graphql.apollomodel.SaveTermsAndConditionsCreditMutation
import com.multimoney.domain.model.credit.SaveTermsAndConditionsCredit

private fun SaveTermsAndConditionsCreditMutation.SaveTermsAndConditionsCredit.mapToDomainModel() = SaveTermsAndConditionsCredit(
    status = status,
    message = message,
    detail = detail
)

fun SaveTermsAndConditionsCreditMutation.Data.mapToDomainModel() = saveTermsAndConditionsCredit.mapToDomainModel()