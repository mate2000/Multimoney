package com.multimoney.data.mapper.credit

import com.multimoney.data.networking.credit.apollomodel.CompanyAddressSVQuery
import com.multimoney.domain.model.credit.CatalogSubOptions
import com.multimoney.domain.model.credit.CompanyAddress
import com.multimoney.domain.model.credit.Canton
import com.multimoney.domain.model.credit.Province

private fun CompanyAddressSVQuery.CompanyProvince.mapToDomainModel() = Province(
    id = pk_Suv_Cat_Opcion_Pregunta_Solicitud_Credito,
    description = descripcion,
    subOptions = subOpciones?.map { it?.mapToDomainModel() } ?: listOf()
)

private fun CompanyAddressSVQuery.SubOpcione.mapToDomainModel() = CatalogSubOptions(
    id = pk_Suv_Cat_Opcion_Pregunta_Solicitud_Credito,
    controlType = tipo_Control,
    description = descripcion,
    pkCatalog = pk_Identificador_Catalogo,
    fkCatalog = fk_Identificador_Catalogo,
    intern = interno
)

private fun CompanyAddressSVQuery.CompanyCanton.mapToDomainModel() = Canton(
    id = pk_Suv_Cat_Opcion_Pregunta_Solicitud_Credito,
    description = descripcion,
    subOptions = subOpciones?.map { it?.mapToDomainModel() } ?: listOf()
)

private fun CompanyAddressSVQuery.SubOpcione1.mapToDomainModel() = CatalogSubOptions(
    id = pk_Suv_Cat_Opcion_Pregunta_Solicitud_Credito,
    controlType = tipo_Control,
    description = descripcion,
    pkCatalog = pk_Identificador_Catalogo,
    fkCatalog = fk_Identificador_Catalogo,
    intern = interno
)

fun CompanyAddressSVQuery.Data.mapToDomainModel() =
    CompanyAddress(
        companyProvince?.map { it?.mapToDomainModel() },
        companyCanton?.map { it?.mapToDomainModel() },
        null
    )
