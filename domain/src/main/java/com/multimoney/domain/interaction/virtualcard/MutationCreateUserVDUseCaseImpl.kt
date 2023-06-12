package com.multimoney.domain.interaction.virtualcard

import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.virtualcard.CreateUser
import com.multimoney.domain.repository.VirtualCardRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MutationCreateUserVDUseCaseImpl @Inject constructor(private val virtualCardRepository: VirtualCardRepository) :
    MutationCreateUserVDUseCase {
    override suspend fun invoke(
        identification: String,
        firstName: String,
        secondName: String,
        lastName: String,
        secondLastName: String,
        email: String,
        callerId: String,
        user: String,
        idBrand: Int,
    ): Flow<MultimoneyResult<CreateUser?>> =
        virtualCardRepository.mutationCreateUserVD(
            identification = identification,
            firstName = firstName,
            secondName = secondName,
            lastName = lastName,
            secondLastName = secondLastName,
            email = email,
            callerId = callerId,
            user = user,
            idBrand = idBrand
        )
}