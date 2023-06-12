package com.multimoney.data.mapper.security

import com.multimoney.data.networking.graphql.apollomodel.ValidateOTPMutation
import com.multimoney.domain.model.security.ValidateOTP
import com.multimoney.domain.model.security.ValidateOTPResponse

private fun ValidateOTPMutation.ValideOTP.mapToDomainModel() = ValidateOTP(validateOTP = ValidateOTPResponse( status = status, message = message, detail = detail))

fun ValidateOTPMutation.Data.mapToDomainModel ()= valideOTP.mapToDomainModel()