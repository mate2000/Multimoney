package com.multimoney.data.mapper.security

import com.multimoney.data.networking.graphql.apollomodel.ChangeDeviceMutation
import com.multimoney.domain.model.security.ChangeDevice

fun ChangeDeviceMutation.Data.mapToDomainModel() = ChangeDevice(status = changeDevice.status)
