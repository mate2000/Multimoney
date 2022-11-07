package com.multimoney.data.mapper.security

import com.multimoney.data.networking.graphql.apollomodel.ValidatePinQuery
import com.multimoney.domain.model.security.ValidatePin
import com.multimoney.domain.model.util.error.MessageError

private fun ValidatePinQuery.ValidatePin.mapMessageToDomainModel() = MessageError(
    status = status,
    message = message,
    detail = detail
)

private fun ValidatePinQuery.ValidatePin.mapToDomainModel() = ValidatePin(
    pkUser = pk_suv_mtr_usuario,
    blockedIndicator = indicador_bloqueo,
    messageError = mapMessageToDomainModel()
)

fun ValidatePinQuery.Data.mapToDomainModel() = validatePin.mapToDomainModel()
