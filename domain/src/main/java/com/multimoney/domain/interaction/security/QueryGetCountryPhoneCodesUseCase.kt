package com.multimoney.domain.interaction.security

import com.multimoney.domain.model.security.CountryPhoneCodes
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QueryGetCountryPhoneCodesUseCase {
    suspend operator fun invoke(
        idBrand: Int
    ) : Flow<MultimoneyResult<CountryPhoneCodes>>
}