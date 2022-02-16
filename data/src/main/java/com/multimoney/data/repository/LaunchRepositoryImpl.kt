package com.multimoney.data.repository

import com.multimoney.data.base.BaseRepository
import com.multimoney.data.networking.MultimoneyApi
import com.multimoney.data.networking.mapper.mapToModel
import com.multimoney.domain.model.launch.LaunchConnection
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.LaunchRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LaunchRepositoryImpl @Inject constructor() : BaseRepository(), LaunchRepository {

    override suspend fun getLaunchList(): Flow<MultimoneyResult<LaunchConnection>> = fetchData(
        apolloCall = MultimoneyApi.getLaunchList()
    ).mapToModel()
}
