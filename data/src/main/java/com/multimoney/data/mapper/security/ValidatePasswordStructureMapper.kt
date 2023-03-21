package com.multimoney.data.mapper.security

import com.multimoney.data.networking.graphql.apollomodel.ValidatePasswordStructureQuery
import com.multimoney.domain.model.security.ValidatePasswordStructure

fun ValidatePasswordStructureQuery.Data.mapToDomainModel(): ValidatePasswordStructure {
    return ValidatePasswordStructure(
        data = validatePasswordStructure.data ?: emptyList(),
        status = validatePasswordStructure.status ?: 0,
        message = validatePasswordStructure.message ?: "",
        detail = validatePasswordStructure.detail ?: ""
    )
}