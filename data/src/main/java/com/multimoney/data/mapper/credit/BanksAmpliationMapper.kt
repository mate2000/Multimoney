package com.multimoney.data.mapper.credit

import com.multimoney.data.networking.graphql.apollomodel.BanksAmpliationQuery
import com.multimoney.domain.model.credit.BanksAmpliation
import com.multimoney.domain.model.credit.BanksAmpliationList

private fun BanksAmpliationQuery.ListBank.mapToDomainModel() = BanksAmpliation(
    id.toString().toInt(),
    bank,
    sinpeBit.toString().toIntOrNull(),
    intSubOrigin.toString().toIntOrNull(),
    innerBit.toString().toIntOrNull(),
    sinpeLenth.toString().toIntOrNull(),
    innerMask,
    state,
    suvMtrUser,
    suvCatBrand.toString().toIntOrNull(),
    subOrigin.toString().toIntOrNull(),
    codeACH
)

fun BanksAmpliationQuery.Data.mapToDomainModel() =
    BanksAmpliationList(listBanks.map { it.mapToDomainModel() })