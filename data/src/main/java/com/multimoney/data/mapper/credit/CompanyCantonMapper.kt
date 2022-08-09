package com.multimoney.data.mapper.credit

import com.multimoney.data.networking.credit.apollomodel.CompanyCantonQuery
import com.multimoney.domain.model.credit.CatalogSubOptions
import com.multimoney.domain.model.credit.CompanyCanton

private fun CompanyCantonQuery.CompanyCanton.mapToDomainModel() = CompanyCanton(
    id = pk_Suv_Cat_Opcion_Pregunta_Solicitud_Credito,
    description = descripcion,
    subOptions = subOpciones?.map { it?.mapToDomainModel() } ?: listOf()
)

private fun CompanyCantonQuery.SubOpcione.mapToDomainModel() = CatalogSubOptions(
    id = pk_Suv_Cat_Opcion_Pregunta_Solicitud_Credito,
    controlType = tipo_Control,
    description = descripcion,
    pkCatalog = pk_Identificador_Catalogo,
    fkCatalog = fk_Identificador_Catalogo,
    intern = interno
)

fun CompanyCantonQuery.Data.mapToDomainModel() = companyCanton?.map { it?.mapToDomainModel() }

