package com.multimoney.domain.interaction

import com.multimoney.domain.model.launch.LaunchConnection
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface GetLaunchListUseCase {
    suspend operator fun invoke(): Flow<MultimoneyResult<LaunchConnection>>
}