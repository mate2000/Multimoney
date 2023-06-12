package com.multimoney.data.mapper.security

import com.multimoney.data.networking.graphql.apollomodel.PhoneValidationMutation
import com.multimoney.domain.model.security.PhoneValidation

fun PhoneValidationMutation.PhoneValidation.mapToDomainModel() = PhoneValidation(
    status = status,
    message = message,
    detail = detail
)

fun PhoneValidationMutation.Data.mapToDomainModel() = phoneValidation.mapToDomainModel()