package com.multimoney.data.mapper.virtualcard

import com.multimoney.data.networking.graphql.apollomodel.CardBlockingNVMutation
import com.multimoney.domain.model.virtualcard.CardBlocking

private fun CardBlockingNVMutation.CardBlockingNV.mapToDomainModel() =
    CardBlocking(processedAt = processedAt)

fun CardBlockingNVMutation.Data.mapToDomainModel() = cardBlockingNV.mapToDomainModel()
