package com.multimoney.domain.repository

import com.multimoney.domain.model.launch.LaunchConnection
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface LaunchRepository {
    suspend fun getLaunchList(): Flow<MultimoneyResult<LaunchConnection>>
}