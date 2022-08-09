package com.multimoney.data.mapper.credit

import com.multimoney.data.networking.credit.apollomodel.CompanyProvinceQuery

import com.multimoney.domain.model.credit.CatalogSubOptions
import com.multimoney.domain.model.credit.CompanyProvince

private fun CompanyProvinceQuery.CompanyProvince.mapToDomainModel() = CompanyProvince(
    id = pk_Suv_Cat_Opcion_Pregunta_Solicitud_Credito,
    description = descripcion,
    subOptions = subOpciones?.map { it?.mapToDomainModel() } ?: listOf()
)

private fun CompanyProvinceQuery.SubOpcione.mapToDomainModel() = CatalogSubOptions(
    id = pk_Suv_Cat_Opcion_Pregunta_Solicitud_Credito,
    controlType = tipo_Control,
    description = descripcion,
    pkCatalog = pk_Identificador_Catalogo,
    fkCatalog = fk_Identificador_Catalogo,
    intern = interno
)

fun CompanyProvinceQuery.Data.mapToDomainModel() = companyProvince?.map { it?.mapToDomainModel() }

