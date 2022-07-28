package com.multimoney.data.mapper.security

import com.multimoney.data.networking.security.apollomodel.DataInformationClientQuery
import com.multimoney.domain.model.security.ClientInfoCr

private fun DataInformationClientQuery.DataInformationClient.mapToDomainModel() = ClientInfoCr(
    fullName = nombre,
    status = status
)

fun DataInformationClientQuery.Data.mapToDomainModel() = dataInformationClient?.mapToDomainModel()