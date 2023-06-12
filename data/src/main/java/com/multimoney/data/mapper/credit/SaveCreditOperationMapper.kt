package com.multimoney.data.mapper.credit

import com.multimoney.data.networking.graphql.apollomodel.SaveCreditOperationMutation
import com.multimoney.domain.model.credit.SaveCreditOperation

private fun SaveCreditOperationMutation.SaveCreditOperation.mapToDomainModel() = SaveCreditOperation(
    idPrint = idPrint.toString().toLong(),
    showFinalScreen = showFinalScreen,
    showOutSchedule = showOutSchedule
)

fun SaveCreditOperationMutation.Data.mapToDomainModel() = saveCreditOperation.mapToDomainModel()
