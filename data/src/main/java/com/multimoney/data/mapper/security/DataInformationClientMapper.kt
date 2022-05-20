package com.multimoney.data.mapper.security

import com.multimoney.data.networking.security.apollomodel.DataInformationClientQuery
import com.multimoney.domain.model.security.ClientInfoCr

fun DataInformationClientQuery.DataInformationClient.mapToDomainModel() = ClientInfoCr(
    nombre = nombre,
    tipoIdentificacion = tipoIdentificacion,
    status = status,
    message = message
)