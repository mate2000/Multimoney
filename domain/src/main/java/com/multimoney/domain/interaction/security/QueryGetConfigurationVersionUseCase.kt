package com.multimoney.domain.interaction.security

import com.multimoney.domain.model.security.ConfigurationVersion
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QueryGetConfigurationVersionUseCase {
    suspend operator fun invoke(
        platform: String,
        appVersion: String,
        idBrand: Int
    ): Flow<MultimoneyResult<ConfigurationVersion?>>
}
