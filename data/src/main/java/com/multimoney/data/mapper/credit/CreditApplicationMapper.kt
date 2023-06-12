package com.multimoney.data.mapper.credit

import com.multimoney.data.networking.graphql.apollomodel.SaveCreditApplicationMutation
import com.multimoney.domain.model.credit.CreditApplication
import com.multimoney.domain.model.util.error.MessageError

private fun SaveCreditApplicationMutation.SaveCreditApplication.mapMessageToDomainModel() = MessageError(
    status = status,
    message = message,
    detail = detail
)

private fun SaveCreditApplicationMutation.SaveCreditApplication.mapToDomainModel() = CreditApplication(
    messageError = mapMessageToDomainModel()
)

fun SaveCreditApplicationMutation.Data.mapToDomainModel() = saveCreditApplication.mapToDomainModel()
