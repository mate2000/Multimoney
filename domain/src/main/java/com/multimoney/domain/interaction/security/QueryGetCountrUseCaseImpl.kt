package com.multimoney.domain.interaction.security

import com.multimoney.domain.model.security.CountryList
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SecurityRepository
import kotlinx.coroutines.flow.Flow

class QueryGetCountryUseCaseImpl(private val serviceRepository: SecurityRepository) :
    QueryGetCountryUseCase {
    override suspend fun invoke(
        user: String
    ): Flow<MultimoneyResult<CountryList?>> = serviceRepository.queryGetCountry(user)
}