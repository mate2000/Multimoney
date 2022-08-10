package com.multimoney.data.mapper.credit

import com.multimoney.data.networking.credit.apollomodel.CompanyDistrictQuery
import com.multimoney.domain.model.credit.CatalogSubOptions
import com.multimoney.domain.model.credit.CompanyDistrict

private fun CompanyDistrictQuery.CompanyDistrict.mapToDomainModel() = CompanyDistrict(
    id = pk_Suv_Cat_Opcion_Pregunta_Solicitud_Credito,
    description = descripcion,
    subOptions = subOpciones?.map { it?.mapToDomainModel() } ?: listOf()
)

private fun CompanyDistrictQuery.SubOpcione.mapToDomainModel() = CatalogSubOptions(
    id = pk_Suv_Cat_Opcion_Pregunta_Solicitud_Credito,
    controlType = tipo_Control,
    description = descripcion,
    pkCatalog = pk_Identificador_Catalogo,
    fkCatalog = fk_Identificador_Catalogo,
    intern = interno
)

fun CompanyDistrictQuery.Data.mapToDomainModel() = companyDistrict?.map { it?.mapToDomainModel() }

