package com.multimoney.data.mapper.security

import com.multimoney.data.networking.graphql.apollomodel.UserPhoneMobileSaveMutation
import com.multimoney.domain.model.security.UserPhoneMobileSave

private fun UserPhoneMobileSaveMutation.UserPhoneMobileSave.mapToDomainModel() = UserPhoneMobileSave(
    pkUserMobile = pK_SUV_LOG_USUARIO_EVENTO_MOBILE.toString().toLong(),
    platform = plataforma,
    uuid = uuid,
    deviceVersion = version_Dispositivo,
    manufacture = manofactura,
    deviceName = nombre_Dispositivo,
    serialNumber = numero_Serie,
    ipAddress = direccion_Ip,
    latitude = latitud,
    longitude = longitud,
    tokenNotificationsPush = tokenNotificationsPush,
    deviceCard = dISPOSITIVO_TARJETA,
    walletId = iD_BILLETERA_TARJETA,
    status = status,
    message = message,
    detail = detail
)

fun UserPhoneMobileSaveMutation.Data.mapToDomainModel() = userPhoneMobileSave.mapToDomainModel()
