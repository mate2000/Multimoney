package com.multimoney.domain.interaction.profile

import com.multimoney.domain.model.profile.CountryContact
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow

class QueryCountryContactUseCaseImpl(val repository: ProfileRepository) :
    QueryCountryContactUseCase {
    override suspend fun invoke(
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<CountryContact?>> = repository.queryCountryContact(user, idBrand)
}