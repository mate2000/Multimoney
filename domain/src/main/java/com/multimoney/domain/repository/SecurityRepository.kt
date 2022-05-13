package com.multimoney.domain.repository

import com.multimoney.domain.model.security.User
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface SecurityRepository {

    suspend fun mutationUserValidation(
        email: String,
        currentStep: String,
        idBrand: Int
    ): Flow<MultimoneyResult<User?>>

}