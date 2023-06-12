package com.multimoney.data.mapper.security

import com.multimoney.data.networking.graphql.apollomodel.GetCompanyNameByIdentificationQuery
import com.multimoney.domain.model.security.Company

private fun GetCompanyNameByIdentificationQuery.GetCompanyNameByIdentification.mapToDomainModel() = Company(
    name = nombre,
    message = message,
    status = status.toString().toIntOrNull()
)

fun GetCompanyNameByIdentificationQuery.Data.mapToDomainModel() = getCompanyNameByIdentification?.mapToDomainModel()
