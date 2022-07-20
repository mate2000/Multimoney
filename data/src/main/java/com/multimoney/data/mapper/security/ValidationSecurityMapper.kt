package com.multimoney.data.mapper.security

import com.multimoney.data.networking.security.apollomodel.ValidationSecurityQuery
import com.multimoney.domain.model.security.ValidateSecurity
import com.multimoney.domain.model.util.error.MessageError

fun ValidationSecurityQuery.ValidateSecurity.mapMessageToDomainModel() = MessageError(
    status = status,
    message = message,
    detail = detail
)

fun ValidationSecurityQuery.ValidateSecurity.mapToDomainModel() = ValidateSecurity(
    messageError = mapMessageToDomainModel()
)

fun ValidationSecurityQuery.Data.mapToDomainModel() = validateSecurity?.mapToDomainModel()