package com.multimoney.data.mapper

import com.multimoney.data.networking.apollomodel.LaunchListQuery
import com.multimoney.domain.model.launch.Launch
import com.multimoney.domain.model.launch.LaunchConnection

fun LaunchListQuery.Launch.mapToDomainModel() = Launch(id, site)

fun LaunchListQuery.Launches.mapToDomainModel() =
    LaunchConnection(cursor, hasMore, launches.map { it?.mapToDomainModel() })
