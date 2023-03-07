package com.multimoney.data.mapper.virtualcard

import com.multimoney.data.networking.graphql.apollomodel.GetParametersMobileByCategoryQuery
import com.multimoney.domain.model.virtualcard.GetParametersMobileByCategory

private fun GetParametersMobileByCategoryQuery.GetParameterMobileByCategory.mapToDomainModel() = GetParametersMobileByCategory(
    pkParameter = pk_Suv_Par_Parametro,
    searchKey = llave_Busqueda,
    description = descripcion,
    value = valor,
    category = categoria,
    editable = editable,
    startValue = valor_Inicio,
    ipAddress = direccion_IP,
    startDescription = descripcion_Inicio,
    applicationOption = optionAplication,
    origin = origen
)

fun GetParametersMobileByCategoryQuery.Data.mapToDomainModel() =  getParameterMobileByCategory.map { it.mapToDomainModel() }