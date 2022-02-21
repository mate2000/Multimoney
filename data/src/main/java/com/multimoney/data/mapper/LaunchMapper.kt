package com.multimoney.data.mapper

import com.multimoney.data.networking.apollomodel.LaunchListQuery
import com.multimoney.data.util.MapperConstants
import com.multimoney.data.util.onMapping
import com.multimoney.domain.model.launch.Launch
import com.multimoney.domain.model.launch.LaunchConnection
import com.multimoney.domain.model.util.HttpError
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun LaunchListQuery.Launch.mapToDomain() = Launch(id, site)

fun LaunchListQuery.Launches.mapToDomain() =
    LaunchConnection(cursor, hasMore, launches.map { it?.mapToDomain() })

fun Flow<MultimoneyResult<LaunchListQuery.Data>>.mapToDomain() =
    map { it.onMapping { data -> MultimoneyResult.Success(data.launches.mapToDomain()) } }

fun Flow<MultimoneyResult<Any>>.mapLaunchListQueryCacheToDomain() = map { result ->
    result.onMapping { data ->
        when (data) {
            is LaunchConnection -> {
                MultimoneyResult.Success(data)
            }
            is LaunchListQuery.Data -> {
                MultimoneyResult.Success(data.launches.mapToDomain())
            }
            else -> {
                MultimoneyResult.Failure(httpError = HttpError(Throwable(MapperConstants.MapperFailed.message)))
            }
        }
    }
}
