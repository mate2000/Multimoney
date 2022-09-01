package com.multimoney.data.mapper.credit

import com.multimoney.data.networking.credit.apollomodel.HomeAddressSVQuery
import com.multimoney.domain.model.credit.Canton
import com.multimoney.domain.model.credit.CatalogSubOptions
import com.multimoney.domain.model.credit.HomeAddress
import com.multimoney.domain.model.credit.Province

private fun HomeAddressSVQuery.HomeProvince.mapToDomainModel() = Province(
    id = pk_Suv_Cat_Opcion_Pregunta_Solicitud_Credito,
    description = descripcion,
    subOptions = subOpciones?.map { it?.mapToDomainModel() } ?: listOf()
)

private fun HomeAddressSVQuery.SubOpcione.mapToDomainModel() = CatalogSubOptions(
    id = pk_Suv_Cat_Opcion_Pregunta_Solicitud_Credito,
    controlType = tipo_Control,
    description = descripcion,
    pkCatalog = pk_Identificador_Catalogo,
    fkCatalog = fk_Identificador_Catalogo,
    intern = interno
)

private fun HomeAddressSVQuery.HomeCanton.mapToDomainModel() = Canton(
    id = pk_Suv_Cat_Opcion_Pregunta_Solicitud_Credito,
    description = descripcion,
    subOptions = subOpciones?.map { it?.mapToDomainModel() } ?: listOf()
)

private fun HomeAddressSVQuery.SubOpcione1.mapToDomainModel() = CatalogSubOptions(
    id = pk_Suv_Cat_Opcion_Pregunta_Solicitud_Credito,
    controlType = tipo_Control,
    description = descripcion,
    pkCatalog = pk_Identificador_Catalogo,
    fkCatalog = fk_Identificador_Catalogo,
    intern = interno
)

fun HomeAddressSVQuery.Data.mapToDomainModel() =
    HomeAddress(
        homeProvince?.map { it?.mapToDomainModel() },
        homeCanton?.map { it?.mapToDomainModel() },
        null
    )
