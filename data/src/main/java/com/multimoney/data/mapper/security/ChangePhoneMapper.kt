package com.multimoney.data.mapper.security

import com.multimoney.data.networking.graphql.apollomodel.ChangePhoneMutation
import com.multimoney.data.networking.graphql.apollomodel.ValidateOTPMutation
import com.multimoney.domain.model.security.ChangePhone
import com.multimoney.domain.model.security.ChangePhoneResponse
import com.multimoney.domain.model.security.ValidateOTP
import com.multimoney.domain.model.security.ValidateOTPResponse


private fun ChangePhoneMutation.ChangePhone.mapToDomainModel() = ChangePhone(changePhone = ChangePhoneResponse( status = status))

fun ChangePhoneMutation.Data.mapToDomainModel ()= changePhone.mapToDomainModel()