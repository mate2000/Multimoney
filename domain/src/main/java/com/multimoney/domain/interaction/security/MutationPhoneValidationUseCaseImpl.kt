package com.multimoney.domain.interaction.security

import com.multimoney.domain.model.security.PhoneValidation
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SecurityRepository
import kotlinx.coroutines.flow.Flow

class MutationPhoneValidationUseCaseImpl(val securityRepository: SecurityRepository) : MutationPhoneValidationUseCase {
    override suspend fun invoke(
        phone: String?,
        identification: String?,
        idBrand: Int
    ): Flow<MultimoneyResult<PhoneValidation?>> = securityRepository.mutationPhoneValidation(phone, identification, idBrand)
}