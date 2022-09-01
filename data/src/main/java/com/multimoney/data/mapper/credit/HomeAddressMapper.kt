package com.multimoney.data.mapper.credit

import com.multimoney.data.networking.credit.apollomodel.HomeAddressQuery
import com.multimoney.domain.model.credit.Canton
import com.multimoney.domain.model.credit.CatalogSubOptions
import com.multimoney.domain.model.credit.District
import com.multimoney.domain.model.credit.HomeAddress
import com.multimoney.domain.model.credit.Province

private fun HomeAddressQuery.HomeProvince.mapToDomainModel() = Province(
    id = pk_Suv_Cat_Opcion_Pregunta_Solicitud_Credito,
    description = descripcion,
    subOptions = subOpciones?.map { it?.mapToDomainModel() } ?: listOf()
)

private fun HomeAddressQuery.SubOpcione.mapToDomainModel() = CatalogSubOptions(
    id = pk_Suv_Cat_Opcion_Pregunta_Solicitud_Credito,
    controlType = tipo_Control,
    description = descripcion,
    pkCatalog = pk_Identificador_Catalogo,
    fkCatalog = fk_Identificador_Catalogo,
    intern = interno
)

private fun HomeAddressQuery.HomeCanton.mapToDomainModel() = Canton(
    id = pk_Suv_Cat_Opcion_Pregunta_Solicitud_Credito,
    description = descripcion,
    subOptions = subOpciones?.map { it?.mapToDomainModel() } ?: listOf()
)

private fun HomeAddressQuery.SubOpcione1.mapToDomainModel() = CatalogSubOptions(
    id = pk_Suv_Cat_Opcion_Pregunta_Solicitud_Credito,
    controlType = tipo_Control,
    description = descripcion,
    pkCatalog = pk_Identificador_Catalogo,
    fkCatalog = fk_Identificador_Catalogo,
    intern = interno
)

private fun HomeAddressQuery.HomeDistrict.mapToDomainModel() = District(
    id = pk_Suv_Cat_Opcion_Pregunta_Solicitud_Credito,
    description = descripcion,
    subOptions = subOpciones?.map { it?.mapToDomainModel() } ?: listOf()
)

private fun HomeAddressQuery.SubOpcione2.mapToDomainModel() = CatalogSubOptions(
    id = pk_Suv_Cat_Opcion_Pregunta_Solicitud_Credito,
    controlType = tipo_Control,
    description = descripcion,
    pkCatalog = pk_Identificador_Catalogo,
    fkCatalog = fk_Identificador_Catalogo,
    intern = interno
)

fun HomeAddressQuery.Data.mapToDomainModel() =
    HomeAddress(
        homeProvince?.map { it?.mapToDomainModel() },
        homeCanton?.map { it?.mapToDomainModel() },
        homeDistrict?.map { it?.mapToDomainModel() }
    )
