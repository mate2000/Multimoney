package com.multimoney.domain.interaction.security

import com.multimoney.domain.model.security.ValidatePin
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SecurityRepository
import kotlinx.coroutines.flow.Flow

class QueryValidatePinUseCaseImpl(val repository: SecurityRepository) : QueryValidatePinUseCase {

    override suspend fun invoke(
        idBrand: Int,
        appSource: Int,
        pkUser: String,
        ip: String?,
        pinSecurity: String,
        telephone: String?,
        sendValidatePin: String,
        flowOrigination: String,
        userCreate: String
    ): Flow<MultimoneyResult<ValidatePin?>> = repository.queryValidatePin(
        idBrand,
        appSource,
        pkUser,
        ip,
        pinSecurity,
        telephone,
        sendValidatePin,
        flowOrigination,
        userCreate
    )
}