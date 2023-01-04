package com.multimoney.data.mapper.virtualcard

import com.multimoney.data.networking.graphql.apollomodel.CardUnblockingNVMutation
import com.multimoney.domain.model.virtualcard.CardUnblocking

private fun CardUnblockingNVMutation.CardUnblockingNV.mapToDomainModel() =
    CardUnblocking(processedAt = processedAt)

fun CardUnblockingNVMutation.Data.mapToDomainModel() = cardUnblockingNV.mapToDomainModel()
