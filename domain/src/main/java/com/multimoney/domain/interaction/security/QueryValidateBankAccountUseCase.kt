package com.multimoney.domain.interaction.security

import com.multimoney.domain.model.security.ValidateAccount
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QueryValidateBankAccountUseCase {
    suspend operator fun invoke(
        account: String,
        identification: String,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<ValidateAccount?>>
}