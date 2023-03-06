package com.multimoney.data.mapper.security

import com.multimoney.data.networking.graphql.apollomodel.SaveLogTrackingMutation
import com.multimoney.domain.model.security.SaveLogTracking

fun SaveLogTrackingMutation.Data.mapToDomainModel() = SaveLogTracking(
    status = saveLogTracking.status,
    message = saveLogTracking.message,
    detail = saveLogTracking.detail
)
