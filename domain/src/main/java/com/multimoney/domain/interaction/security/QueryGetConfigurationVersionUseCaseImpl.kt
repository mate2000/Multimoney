package com.multimoney.domain.interaction.security

import com.multimoney.domain.model.security.ConfigurationVersion
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SecurityRepository
import kotlinx.coroutines.flow.Flow

class QueryGetConfigurationVersionUseCaseImpl(val securityRepository: SecurityRepository) :
    QueryGetConfigurationVersionUseCase {
    override suspend fun invoke(
        platform: String,
        appVersion: String,
        idBrand: Int
    ): Flow<MultimoneyResult<ConfigurationVersion?>> = securityRepository.queryGetConfigurationVersion(
        platform,
        appVersion,
        idBrand
    )
}
