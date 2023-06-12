package com.multimoney.data.mapper.mmvisa

import com.multimoney.data.networking.graphql.apollomodel.DeleteTokenDeviceNVMutation
import com.multimoney.domain.model.mmvisa.CardIssuanceNV

fun DeleteTokenDeviceNVMutation.DeleteTokenDeviceNV.mapToDomainModel() = CardIssuanceNV(
    rc = rc,
    msg = msg,
    processAt = processedAt
)

fun DeleteTokenDeviceNVMutation.Data.mapToDomainModel() = deleteTokenDeviceNV.mapToDomainModel()
