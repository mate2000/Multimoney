package com.multimoney.data.mapper.security

import com.multimoney.data.networking.security.apollomodel.GetCompanyNameByIdentificationQuery
import com.multimoney.domain.model.security.Company

private fun GetCompanyNameByIdentificationQuery.GetCompanyNameByIdentification.mapToDomainModel() = Company(
    name = nombre
)

fun GetCompanyNameByIdentificationQuery.Data.mapToDomainModel() = getCompanyNameByIdentification?.mapToDomainModel()
