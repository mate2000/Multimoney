package com.multimoney.domain.interaction.security

import com.multimoney.domain.model.security.ChangePhone
import com.multimoney.domain.model.security.OnfidoCheckProcess
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SecurityRepository
import kotlinx.coroutines.flow.Flow

class MutationChangePhoneUseCaseImpl(
    val securityRepository: SecurityRepository
) : MutationChangePhoneUseCase {
    override suspend fun invoke(
        identification: String,
        phone : String,
        pkUser: String,
        idBrand: Int,
        user: String

    ): Flow<MultimoneyResult<ChangePhone>> = securityRepository.mutationChangePhone(
        identification,
        phone,
        pkUser,
        idBrand,
        user
    )
}