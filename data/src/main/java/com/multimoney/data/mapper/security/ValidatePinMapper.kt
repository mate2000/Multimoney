package com.multimoney.data.mapper.security

import com.multimoney.data.networking.security.apollomodel.ValidatePinQuery
import com.multimoney.domain.model.security.ValidatePin
import com.multimoney.domain.model.util.error.MessageError

fun ValidatePinQuery.ValidatePin.mapMessageToDomainModel() = MessageError(
    status = status,
    message = message,
    detail = detail
)

fun ValidatePinQuery.ValidatePin.mapToDomainModel() = ValidatePin(
    pkUser = pk_suv_mtr_usuario,
    blockedIndicator = indicador_bloqueo,
    messageError = mapMessageToDomainModel()
)

fun ValidatePinQuery.Data.toDomainModel() = validatePin?.mapToDomainModel()
