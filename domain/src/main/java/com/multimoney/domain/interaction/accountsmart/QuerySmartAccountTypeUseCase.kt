package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.SmartAccountTypeResult
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QuerySmartAccountTypeUseCase {
    suspend operator fun invoke(
        idBrand: Int,
        user: String
    ): Flow<MultimoneyResult<SmartAccountTypeResult?>>
}