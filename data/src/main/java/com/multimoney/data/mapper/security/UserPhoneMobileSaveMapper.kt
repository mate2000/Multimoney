package com.multimoney.data.mapper.security

import com.multimoney.data.networking.graphql.apollomodel.UserPhoneMobileSaveMutation
import com.multimoney.domain.model.security.UserPhoneMobileSave

private fun UserPhoneMobileSaveMutation.UserPhoneMobileSave.mapToDomainModel() = UserPhoneMobileSave(
    status = status
)

fun UserPhoneMobileSaveMutation.Data.mapToDomainModel() = userPhoneMobileSave.mapToDomainModel()
