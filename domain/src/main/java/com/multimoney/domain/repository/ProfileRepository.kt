package com.multimoney.domain.repository

import com.multimoney.domain.model.profile.CountryContact
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    suspend fun queryCountryContact(
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<CountryContact?>>
}