package com.multimoney.domain.interaction.security

import com.multimoney.domain.model.security.PhoneValidation
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface MutationPhoneValidationUseCase {
    suspend operator fun invoke(
        phone: String?,
        identification: String?,
        idBrand: Int
    ): Flow<MultimoneyResult<PhoneValidation?>>
}