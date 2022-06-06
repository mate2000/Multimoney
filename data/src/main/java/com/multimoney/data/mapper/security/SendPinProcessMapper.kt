package com.multimoney.data.mapper.security

import com.multimoney.data.networking.security.apollomodel.SendPinProcessMutation
import com.multimoney.domain.model.security.SendPinResponse

fun SendPinProcessMutation.SendPinProccess.mapToDomainModel() = SendPinResponse(
    pkUser = pk_suv_mtr_usuario,
    email = correo_electronico,
    userName = username,
    phone = telefono,
    identification = identificacion,
    numberOfPinForwards = cantidad_reenvios_pin,
    numberOfOtpForwards = cantidad_reenvios_otp,
    pinExpirationTime = valor_tiempo_exp_pin
)

fun SendPinProcessMutation.Data.mapToDomainModel() = sendPinProccess?.mapToDomainModel()