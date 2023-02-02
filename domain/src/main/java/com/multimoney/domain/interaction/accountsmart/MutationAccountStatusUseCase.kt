package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.SmartAccountStatusResult
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface MutationAccountStatusUseCase {
    suspend fun invoke(
        user: String,
        idBrand: Int,
        identificationNumber: String,
        newState: String,
        typeState: String,
        idAccountSysde: Long,
        idAccountRequest: Long
    ): Flow<MultimoneyResult<SmartAccountStatusResult?>>
}