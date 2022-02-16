package com.multimoney.data.networking.mapper

import com.multimoney.data.networking.apollomodel.LaunchListQuery
import com.multimoney.data.util.onMapping
import com.multimoney.domain.model.launch.Launch
import com.multimoney.domain.model.launch.LaunchConnection
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun LaunchListQuery.Launch.mapToModel() = Launch(id, site)

fun LaunchListQuery.Launches.mapToModel() =
    LaunchConnection(cursor, hasMore, launches.map { it?.mapToModel() })

fun Flow<MultimoneyResult<LaunchListQuery.Data>>.mapToModel() =
    map { it.onMapping { data -> MultimoneyResult.Success(data.launches.mapToModel()) } }
