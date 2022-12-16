package com.multimoney.data.mapper.credit

import com.multimoney.data.networking.graphql.apollomodel.SaveCreditOfferMutation
import com.multimoney.domain.model.credit.SaveCreditOffer

private fun SaveCreditOfferMutation.SaveCreditOffer.mapToDomainModel() = SaveCreditOffer(
    rejectedBlaze = rejectedBlaze
)

fun SaveCreditOfferMutation.Data.mapToDomainModel() = saveCreditOffer.mapToDomainModel()