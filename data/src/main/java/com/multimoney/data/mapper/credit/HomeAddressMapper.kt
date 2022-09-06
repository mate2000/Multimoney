package com.multimoney.data.mapper.credit

import com.multimoney.data.networking.credit.apollomodel.HomeAddressQuery
import com.multimoney.domain.model.credit.Canton
import com.multimoney.domain.model.credit.CreditCatalogOption
import com.multimoney.domain.model.credit.District
import com.multimoney.domain.model.credit.HomeAddress
import com.multimoney.domain.model.credit.Province

private fun HomeAddressQuery.HomeProvince.mapToDomainModel() = Province(
    id = pk_Suv_Cat_Opcion_Pregunta_Solicitud_Credito,
    description = descripcion,
    subOptions = subOpciones?.map { it?.mapToDomainModel() } ?: listOf()
)

private fun HomeAddressQuery.SubOpcione.mapToDomainModel() = CreditCatalogOption(
    id = pk_Suv_Cat_Opcion_Pregunta_Solicitud_Credito,
    description = descripcion
)

private fun HomeAddressQuery.HomeCanton.mapToDomainModel() = Canton(
    id = pk_Suv_Cat_Opcion_Pregunta_Solicitud_Credito,
    description = descripcion,
    subOptions = subOpciones?.map { it?.mapToDomainModel() } ?: listOf()
)

private fun HomeAddressQuery.SubOpcione1.mapToDomainModel() = CreditCatalogOption(
    id = pk_Suv_Cat_Opcion_Pregunta_Solicitud_Credito,
    description = descripcion
)

private fun HomeAddressQuery.HomeDistrict.mapToDomainModel() = District(
    id = pk_Suv_Cat_Opcion_Pregunta_Solicitud_Credito,
    description = descripcion,
    subOptions = subOpciones?.map { it?.mapToDomainModel() } ?: listOf()
)

private fun HomeAddressQuery.SubOpcione2.mapToDomainModel() = CreditCatalogOption(
    id = pk_Suv_Cat_Opcion_Pregunta_Solicitud_Credito,
    description = descripcion
)

fun HomeAddressQuery.Data.mapToDomainModel() =
    HomeAddress(
        homeProvince?.map { it?.mapToDomainModel() },
        homeCanton?.map { it?.mapToDomainModel() },
        homeDistrict?.map { it?.mapToDomainModel() }
    )
