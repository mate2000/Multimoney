package com.multimoney.domain.interaction.accountsmart

import androidx.paging.PagingData
import com.multimoney.domain.model.accountsmart.SmartMovement
import kotlinx.coroutines.flow.Flow

interface QueryGetPagedSmartMovementsUseCase {
    suspend operator fun invoke(
        user: String,
        idBrand: Int,
        identificationNumber: String,
        accountToken: Long,
        monthDate: String?
    ): Flow<PagingData<SmartMovement>>
}