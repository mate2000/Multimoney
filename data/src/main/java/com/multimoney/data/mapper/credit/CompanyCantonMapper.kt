package com.multimoney.data.mapper.credit

import com.multimoney.data.networking.credit.apollomodel.CompanyCantonQuery
import com.multimoney.domain.model.credit.CreditCatalog
import com.multimoney.domain.model.credit.CreditCatalogOption

private fun CompanyCantonQuery.CompanyCanton.mapToDomainModel() = CreditCatalog(
    pkQuestionOption = pk_Suv_Cat_Opcion_Pregunta_Solicitud_Credito,
    fkQuestion = fk_Suv_Cat_Pregunta_Solicitud_Credito,
    controlType = tipo_Control,
    description = descripcion,
    pkCatalog = pk_Identificador_Catalogo,
    isCatalogBrandOffice = es_Catalogo_Sucursal,
    useValue = utiliza_Valor,
    isCoreCatalog = es_Catalogo_Core,
    pkForm = llave_Formulario,
    valueCatalog = valor_Catalogo,
    maximumAmount = monto_Maximo,
    value = valor,
    subOptions = subOpciones?.map { it?.mapToDomainModel() }
)

private fun CompanyCantonQuery.SubOpcione.mapToDomainModel() = CreditCatalogOption(
    id = pk_Suv_Cat_Opcion_Pregunta_Solicitud_Credito,
    description = descripcion,
    pkCatalog = pk_Identificador_Catalogo,
    fkCatalog = fk_Identificador_Catalogo
)

fun CompanyCantonQuery.Data.mapToDomainModel() = companyCanton?.map { it?.mapToDomainModel() }