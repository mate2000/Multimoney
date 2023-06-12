package com.multimoney.domain.interaction.security

import com.multimoney.domain.model.security.CountryPhoneCodes
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SecurityRepository
import kotlinx.coroutines.flow.Flow


class QueryGetCountryPhoneCodesUseCaseImpl(val securityRepository: SecurityRepository) :
    QueryGetCountryPhoneCodesUseCase {
    override suspend fun invoke(
        idBrand : Int,
    ): Flow<MultimoneyResult<CountryPhoneCodes>> =
        securityRepository.queryGetCountryPhoneCodes(idBrand)
}