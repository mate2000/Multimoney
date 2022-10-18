package com.multimoney.data.mapper.credit

import com.multimoney.data.networking.credit.apollomodel.BanksAndRegularExpressionQuery
import com.multimoney.domain.model.credit.BanksAndRegularExpression
import com.multimoney.domain.model.credit.CreditCatalog
import com.multimoney.domain.model.credit.CreditCatalogOption
import com.multimoney.domain.model.credit.RegularExpression

private fun BanksAndRegularExpressionQuery.Bank.mapToDomainModel() = CreditCatalog(
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

private fun BanksAndRegularExpressionQuery.RegularExpression.mapToDomainModel() = RegularExpression(
    pkRegularExpression = iNT_PK_VEN_CAT_EXPRESION_REGULAR,
    key = sTR_LLAVE_04,
    description = sTR_DESCRIPCION,
    regularExpression = sTR_PATRON_EXPRESION,
    fkRegularExpression = iNT_FK_LLAVE_FORANEA,
    idTypeAccount = iNT_ID_TIPO_CUENTA_DESEMBOLSO
)

private fun BanksAndRegularExpressionQuery.SubOpcione.mapToDomainModel() = CreditCatalogOption(
    description = descripcion,
    pkCatalog = pk_Identificador_Catalogo,
    fkCatalog = fk_Identificador_Catalogo
)

fun BanksAndRegularExpressionQuery.Data.mapToDomainModel() = BanksAndRegularExpression(
    banks = banks?.map { it?.mapToDomainModel() },
    regularExpression = regularExpression?.map { it?.mapToDomainModel() }
)


