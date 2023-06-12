package com.multimoney.data.mapper.security

import com.multimoney.data.networking.graphql.apollomodel.ChangeEmailMutation
import com.multimoney.data.networking.graphql.apollomodel.ChangePhoneMutation
import com.multimoney.domain.model.security.ChangeEmail
import com.multimoney.domain.model.security.ChangeEmailResponse
import com.multimoney.domain.model.security.ChangePhone
import com.multimoney.domain.model.security.ChangePhoneResponse


private fun ChangeEmailMutation.ChangeEmail.mapToDomainModel() = ChangeEmail(changeEmail = ChangeEmailResponse( status = status))

fun ChangeEmailMutation.Data.mapToDomainModel() = changeEmail.mapToDomainModel()

