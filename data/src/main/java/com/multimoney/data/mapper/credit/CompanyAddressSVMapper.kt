package com.multimoney.data.mapper.credit

import com.multimoney.data.networking.credit.apollomodel.CompanyAddressSVQuery
import com.multimoney.domain.model.credit.CreditCatalogOption
import com.multimoney.domain.model.credit.CompanyAddress
import com.multimoney.domain.model.credit.Canton
import com.multimoney.domain.model.credit.Province

private fun CompanyAddressSVQuery.CompanyProvince.mapToDomainModel() = Province(
    id = pk_Suv_Cat_Opcion_Pregunta_Solicitud_Credito,
    description = descripcion,
    subOptions = subOpciones?.map { it?.mapToDomainModel() } ?: listOf()
)

private fun CompanyAddressSVQuery.SubOpcione.mapToDomainModel() = CreditCatalogOption(
    id = pk_Identificador_Catalogo,
    description = descripcion
)

private fun CompanyAddressSVQuery.CompanyCanton.mapToDomainModel() = Canton(
    id = pk_Suv_Cat_Opcion_Pregunta_Solicitud_Credito,
    description = descripcion,
    subOptions = subOpciones?.map { it?.mapToDomainModel() } ?: listOf()
)

private fun CompanyAddressSVQuery.SubOpcione1.mapToDomainModel() = CreditCatalogOption(
    id = pk_Identificador_Catalogo,
    description = descripcion
)

fun CompanyAddressSVQuery.Data.mapToDomainModel() =
    CompanyAddress(
        companyProvince?.map { it?.mapToDomainModel() },
        companyCanton?.map { it?.mapToDomainModel() },
        null
    )
