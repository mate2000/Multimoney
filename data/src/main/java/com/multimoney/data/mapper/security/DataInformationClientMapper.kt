package com.multimoney.data.mapper.security

import com.multimoney.data.networking.graphql.apollomodel.DataInformationClientQuery
import com.multimoney.domain.model.security.ClientInfoCr

private fun DataInformationClientQuery.DataInformationClient.mapToDomainModel() = ClientInfoCr(
    name = nombre,
    firstName = nombre1,
    secondName = nombre2,
    firstLastName = apellido1,
    secondLastName = apellido2,
    status = status,
    message = message
)

fun DataInformationClientQuery.Data.mapToDomainModel() = dataInformationClient.mapToDomainModel()
