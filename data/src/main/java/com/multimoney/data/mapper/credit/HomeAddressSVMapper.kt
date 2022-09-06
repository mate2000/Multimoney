package com.multimoney.data.mapper.credit

import com.multimoney.data.networking.credit.apollomodel.HomeAddressSVQuery
import com.multimoney.domain.model.credit.Canton
import com.multimoney.domain.model.credit.CreditCatalogOption
import com.multimoney.domain.model.credit.HomeAddress
import com.multimoney.domain.model.credit.Province

private fun HomeAddressSVQuery.HomeProvince.mapToDomainModel() = Province(
    id = pk_Suv_Cat_Opcion_Pregunta_Solicitud_Credito,
    description = descripcion,
    subOptions = subOpciones?.map { it?.mapToDomainModel() } ?: listOf()
)

private fun HomeAddressSVQuery.SubOpcione.mapToDomainModel() = CreditCatalogOption(
    id = pk_Suv_Cat_Opcion_Pregunta_Solicitud_Credito,
    description = descripcion
)

private fun HomeAddressSVQuery.HomeCanton.mapToDomainModel() = Canton(
    id = pk_Suv_Cat_Opcion_Pregunta_Solicitud_Credito,
    description = descripcion,
    subOptions = subOpciones?.map { it?.mapToDomainModel() } ?: listOf()
)

private fun HomeAddressSVQuery.SubOpcione1.mapToDomainModel() = CreditCatalogOption(
    id = pk_Suv_Cat_Opcion_Pregunta_Solicitud_Credito,
    description = descripcion
)

fun HomeAddressSVQuery.Data.mapToDomainModel() =
    HomeAddress(
        homeProvince?.map { it?.mapToDomainModel() },
        homeCanton?.map { it?.mapToDomainModel() },
        null
    )
