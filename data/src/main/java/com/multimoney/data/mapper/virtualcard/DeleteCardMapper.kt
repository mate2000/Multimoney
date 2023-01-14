package com.multimoney.data.mapper.virtualcard

import com.multimoney.data.networking.graphql.apollomodel.DeleteCardVDMutation
import com.multimoney.domain.model.virtualcard.DeleteCard

private fun DeleteCardVDMutation.DeleteCardVD.mapToDomainModel() = DeleteCard(
    isApproved =  isApproved,
    apiStatus = apiStatus
)

fun DeleteCardVDMutation.Data.mapToDomainModel() =  deleteCardVD.mapToDomainModel()