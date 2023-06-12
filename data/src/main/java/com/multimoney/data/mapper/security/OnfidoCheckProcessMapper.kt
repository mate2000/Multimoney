package com.multimoney.data.mapper.security

import com.multimoney.data.networking.graphql.apollomodel.OnfidoCheckProcessMutation
import com.multimoney.domain.model.security.OnfidoCheckProcess

private fun OnfidoCheckProcessMutation.OnfidoCheckProcess.mapToDomainModel() = OnfidoCheckProcess(
    id = id
)

fun OnfidoCheckProcessMutation.Data.mapToDomainModel() = onfidoCheckProcess.mapToDomainModel()
