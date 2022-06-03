package com.multimoney.data.mapper.security

import com.multimoney.data.networking.security.apollomodel.ValidationSecurityQuery
import com.multimoney.domain.model.security.ValidateSecurity

fun ValidationSecurityQuery.ValidateSecurity.mapToDomainModel() = ValidateSecurity(
    status = status,
    message = message
)

fun ValidationSecurityQuery.Data.mapToDomainModel() = validateSecurity?.mapToDomainModel()