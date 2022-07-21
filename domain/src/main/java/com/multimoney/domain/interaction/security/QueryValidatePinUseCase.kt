package com.multimoney.domain.interaction.security

import com.multimoney.domain.model.security.ValidatePin
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QueryValidatePinUseCase {
    suspend operator fun invoke(
        idBrand: Int,
        appSource: Int,
        pkUser: String,
        ip: String,
        pinSecurity: String,
        telephone: String,
        sendValidatePin: String,
        flowOrigination: String,
        userCreate: String
    ): Flow<MultimoneyResult<ValidatePin?>>
}