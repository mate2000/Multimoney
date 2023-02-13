package com.multimoney.data.mapper.security

import com.multimoney.data.networking.graphql.apollomodel.RequestChangeDeviceMutation
import com.multimoney.domain.model.security.RequestChangeDevice

fun RequestChangeDeviceMutation.Data.mapToDomainModel() = RequestChangeDevice(
    status = requestchangeDevice.status,
    phoneNumber = requestchangeDevice.phoneNumber,
    otpTime = requestchangeDevice.otpTime
)

