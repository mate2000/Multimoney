package com.multimoney.data.mapper.credit

import com.multimoney.data.networking.credit.apollomodel.CompanyAddressQuery
import com.multimoney.domain.model.credit.CatalogSubOptions
import com.multimoney.domain.model.credit.CompanyAddress
import com.multimoney.domain.model.credit.CompanyCanton
import com.multimoney.domain.model.credit.CompanyDistrict
import com.multimoney.domain.model.credit.CompanyProvince

private fun CompanyAddressQuery.CompanyProvince.mapToDomainModel() = CompanyProvince(
    id = pk_Suv_Cat_Opcion_Pregunta_Solicitud_Credito,
    description = descripcion,
    subOptions = subOpciones?.map { it?.mapToDomainModel() } ?: listOf()
)

private fun CompanyAddressQuery.SubOpcione.mapToDomainModel() = CatalogSubOptions(
    id = pk_Suv_Cat_Opcion_Pregunta_Solicitud_Credito,
    controlType = tipo_Control,
    description = descripcion,
    pkCatalog = pk_Identificador_Catalogo,
    fkCatalog = fk_Identificador_Catalogo,
    intern = interno
)

private fun CompanyAddressQuery.CompanyCanton.mapToDomainModel() = CompanyCanton(
    id = pk_Suv_Cat_Opcion_Pregunta_Solicitud_Credito,
    description = descripcion,
    subOptions = subOpciones?.map { it?.mapToDomainModel() } ?: listOf()
)

private fun CompanyAddressQuery.SubOpcione1.mapToDomainModel() = CatalogSubOptions(
    id = pk_Suv_Cat_Opcion_Pregunta_Solicitud_Credito,
    controlType = tipo_Control,
    description = descripcion,
    pkCatalog = pk_Identificador_Catalogo,
    fkCatalog = fk_Identificador_Catalogo,
    intern = interno
)

private fun CompanyAddressQuery.CompanyDistrict.mapToDomainModel() = CompanyDistrict(
    id = pk_Suv_Cat_Opcion_Pregunta_Solicitud_Credito,
    description = descripcion,
    subOptions = subOpciones?.map { it?.mapToDomainModel() } ?: listOf()
)

private fun CompanyAddressQuery.SubOpcione2.mapToDomainModel() = CatalogSubOptions(
    id = pk_Suv_Cat_Opcion_Pregunta_Solicitud_Credito,
    controlType = tipo_Control,
    description = descripcion,
    pkCatalog = pk_Identificador_Catalogo,
    fkCatalog = fk_Identificador_Catalogo,
    intern = interno
)

fun CompanyAddressQuery.Data.mapToDomainModel(isSV: Boolean = false) =
    CompanyAddress(
        companyProvince?.map { it?.mapToDomainModel() },
        companyCanton?.map { it?.mapToDomainModel() },
        companyDistrict?.map { it?.mapToDomainModel() }
    )
