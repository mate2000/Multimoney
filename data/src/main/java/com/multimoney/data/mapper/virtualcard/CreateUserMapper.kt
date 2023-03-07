package com.multimoney.data.mapper.virtualcard

import com.multimoney.data.networking.graphql.apollomodel.CreateUserVDMutation
import com.multimoney.domain.model.virtualcard.CreateUser

private fun CreateUserVDMutation.CreateUserVD.mapToDomainModel() = CreateUser(
    userName = userName,
    password = password
)

fun CreateUserVDMutation.Data.mapToDomainModel() =  createUserVD.mapToDomainModel()