package com.multimoney.domain.interaction.virtualcard

import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.virtualcard.CreateUser
import kotlinx.coroutines.flow.Flow

interface MutationCreateUserVDUseCase {
    suspend operator fun invoke(
        identification: String,
        firstName: String,
        secondName: String,
        lastName: String,
        secondLastName: String,
        email: String,
        callerId: String,
        user: String,
        idBrand: Int,
        accountToken: Long
    ): Flow<MultimoneyResult<CreateUser?>>
}