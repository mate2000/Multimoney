package com.multimoney.data.mapper.security

import com.multimoney.data.networking.graphql.apollomodel.GetTokenQuery
import com.multimoney.domain.model.security.Token
import com.multimoney.domain.model.util.error.MessageError

private fun GetTokenQuery.Token.mapMessageToDomainModel() = MessageError(
    status = status,
    message = message,
    detail = detail
)

private fun GetTokenQuery.Token.mapToDomainModel() = Token(
    accessToken = access_token,
    expiresIn = expires_in,
    tokenType = token_type,
    messageError = mapMessageToDomainModel()
)

fun GetTokenQuery.Data.mapToDomainModel() = token.mapToDomainModel()
