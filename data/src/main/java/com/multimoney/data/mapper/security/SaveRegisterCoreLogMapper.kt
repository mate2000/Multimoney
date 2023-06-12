package com.multimoney.data.mapper.security

import com.multimoney.data.networking.graphql.apollomodel.SaveRegisterCoreLogMutation
import com.multimoney.domain.model.security.SaveRegisterCoreLog

fun SaveRegisterCoreLogMutation.SaveRegisterCoreLog.mapToDomainModel() = SaveRegisterCoreLog(
    status = status,
    message = message,
    detail = detail
)

fun SaveRegisterCoreLogMutation.Data.mapToDomainModel() = saveRegisterCoreLog.mapToDomainModel()