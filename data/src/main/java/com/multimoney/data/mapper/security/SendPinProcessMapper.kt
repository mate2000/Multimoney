package com.multimoney.data.mapper.security

import com.multimoney.data.networking.security.apollomodel.SendPinProcessMutation
import com.multimoney.domain.model.security.SendPinProcess
import com.multimoney.domain.model.util.error.MessageError

fun SendPinProcessMutation.SendPinProccess.mapMessageToDomainModel() = MessageError(
    status = status,
    message = message,
    detail = detail
)

fun SendPinProcessMutation.SendPinProccess.mapToDomainModel() = SendPinProcess(
    pkUser = pk_suv_mtr_usuario,
    email = correo_electronico,
    userName = username,
    phone = telefono,
    identification = identificacion,
    numberOfPinForwards = cantidad_reenvios_pin,
    numberOfOtpForwards = cantidad_reenvios_otp,
    pinExpirationTime = valor_tiempo_exp_pin,
    messageError = mapMessageToDomainModel()
)

fun SendPinProcessMutation.Data.mapToDomainModel() = sendPinProccess?.mapToDomainModel()