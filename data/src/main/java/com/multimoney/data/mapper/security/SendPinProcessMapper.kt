package com.multimoney.data.mapper.security

import com.multimoney.data.networking.graphql.apollomodel.SendPinProcessMutation
import com.multimoney.domain.model.security.SendPinProcess
import com.multimoney.domain.model.util.error.MessageError

private fun SendPinProcessMutation.SendPinProccess.mapMessageToDomainModel() = MessageError(
    status = status,
    message = message,
    detail = detail
)

private fun SendPinProcessMutation.SendPinProccess.mapToDomainModel() = SendPinProcess(
    pkUser = pk_suv_mtr_usuario,
    email = correo_electronico,
    userName = username,
    phone = telefono,
    identification = identificacion,
    numberOfPinForwards = cantidad_reenvios_pin,
    numberOfOtpForwards = cantidad_reenvios_otp,
    pinExpirationTime = valor_tiempo_exp_pin,
    type = tipo_envio,
    nextType = tipo_envio_sig,
    messageError = mapMessageToDomainModel()
)

fun SendPinProcessMutation.Data.mapToDomainModel() = sendPinProccess.mapToDomainModel()
