package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.SmartMovementsResult
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QueryGetCoreBankMovementsUseCase {
    suspend operator fun invoke(
        user: String,
        idBrand: Int,
        identificationNumber: String,
        accountToken: Long,
        pageNumber: Int,
        pageSize: Int,
        monthDate: String
    ): Flow<MultimoneyResult<SmartMovementsResult?>>
}
