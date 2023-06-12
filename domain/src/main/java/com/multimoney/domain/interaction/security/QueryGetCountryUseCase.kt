package com.multimoney.domain.interaction.security

import com.multimoney.domain.model.security.CountryList
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QueryGetCountryUseCase {
    suspend operator fun invoke(user: String): Flow<MultimoneyResult<CountryList?>>
}