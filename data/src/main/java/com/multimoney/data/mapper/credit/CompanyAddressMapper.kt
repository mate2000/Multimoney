package com.multimoney.data.mapper.credit

import com.multimoney.data.networking.credit.apollomodel.CompanyAddressQuery
import com.multimoney.domain.model.credit.CreditCatalogOption
import com.multimoney.domain.model.credit.CompanyAddress
import com.multimoney.domain.model.credit.Canton
import com.multimoney.domain.model.credit.District
import com.multimoney.domain.model.credit.Province

private fun CompanyAddressQuery.CompanyProvince.mapToDomainModel() = Province(
    id = pk_Suv_Cat_Opcion_Pregunta_Solicitud_Credito,
    description = descripcion,
    subOptions = subOpciones?.map { it?.mapToDomainModel() } ?: listOf()
)

private fun CompanyAddressQuery.SubOpcione.mapToDomainModel() = CreditCatalogOption(
    id = pk_Suv_Cat_Opcion_Pregunta_Solicitud_Credito,
    description = descripcion
)

private fun CompanyAddressQuery.CompanyCanton.mapToDomainModel() = Canton(
    id = pk_Suv_Cat_Opcion_Pregunta_Solicitud_Credito,
    description = descripcion,
    subOptions = subOpciones?.map { it?.mapToDomainModel() } ?: listOf()
)

private fun CompanyAddressQuery.SubOpcione1.mapToDomainModel() = CreditCatalogOption(
    id = pk_Suv_Cat_Opcion_Pregunta_Solicitud_Credito,
    description = descripcion,
)

private fun CompanyAddressQuery.CompanyDistrict.mapToDomainModel() = District(
    id = pk_Suv_Cat_Opcion_Pregunta_Solicitud_Credito,
    description = descripcion,
    subOptions = subOpciones?.map { it?.mapToDomainModel() } ?: listOf()
)

private fun CompanyAddressQuery.SubOpcione2.mapToDomainModel() = CreditCatalogOption(
    id = pk_Suv_Cat_Opcion_Pregunta_Solicitud_Credito,
    description = descripcion,
)

fun CompanyAddressQuery.Data.mapToDomainModel() =
    CompanyAddress(
        companyProvince?.map { it?.mapToDomainModel() },
        companyCanton?.map { it?.mapToDomainModel() },
        companyDistrict?.map { it?.mapToDomainModel() }
    )
