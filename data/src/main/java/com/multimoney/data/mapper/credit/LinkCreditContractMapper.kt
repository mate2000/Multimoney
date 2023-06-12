package com.multimoney.data.mapper.credit

import com.multimoney.data.networking.graphql.apollomodel.GetLinkCreditContractQuery
import com.multimoney.domain.model.credit.LinkCreditContract

private fun GetLinkCreditContractQuery.GetLinkCreditContract.mapToDomainModel() = LinkCreditContract(
    linkAvailable = linkAvailable,
    link = link,
    statusEvicertia = statusEvicertia
)

fun GetLinkCreditContractQuery.Data.mapToDomainModel() = getLinkCreditContract.mapToDomainModel()