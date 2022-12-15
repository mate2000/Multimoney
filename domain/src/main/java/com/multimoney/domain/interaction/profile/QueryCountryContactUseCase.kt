package com.multimoney.domain.interaction.profile

import com.multimoney.domain.model.profile.CountryContact
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QueryCountryContactUseCase {
    suspend operator fun invoke(
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<CountryContact?>>
}