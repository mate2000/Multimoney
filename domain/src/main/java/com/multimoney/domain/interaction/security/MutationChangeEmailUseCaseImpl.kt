package com.multimoney.domain.interaction.security
import com.multimoney.domain.model.security.ChangeEmail
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SecurityRepository
import kotlinx.coroutines.flow.Flow

class MutationChangeEmailUseCaseImpl(
    val securityRepository: SecurityRepository
) : MutationChangeEmailUseCase {
    override suspend fun invoke(
        pkUser: Int,
        idClient: Int,
        identification: String,
        email: String,
        registerId: Int,
        changeUser: Boolean,
        user: String,
        idBrand: Int
        ): Flow<MultimoneyResult<ChangeEmail>> = securityRepository.mutationChangeEmail(
        pkUser,
        idClient,
        identification,
        email,
        registerId,
        changeUser,
        user,
        idBrand
    )
}