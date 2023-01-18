package com.multimoney.data.mapper.virtualcard

import com.multimoney.data.networking.graphql.apollomodel.UpdateCardVDMutation
import com.multimoney.domain.model.virtualcard.UpdateCard

private fun UpdateCardVDMutation.UpdateCardVD.mapToDomainModel() = UpdateCard(
    applicationName = applicationName,
    userName = userName,
    isApproved =  isApproved,
    apiStatus = apiStatus,
    default = default
)

fun UpdateCardVDMutation.Data.mapToDomainModel() =  updateCardVD.mapToDomainModel()