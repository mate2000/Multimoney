package com.multimoney.domain.interaction.accountsmart

import androidx.paging.PagingData
import com.multimoney.domain.model.accountsmart.SmartMovement
import com.multimoney.domain.repository.SmartAccountRepository
import kotlinx.coroutines.flow.Flow

class QueryGetPagedSmartMovementsUseCaseUseCaseImpl(val repository: SmartAccountRepository) : QueryGetPagedSmartMovementsUseCase {
    override suspend fun invoke(
        user: String,
        idBrand: Int,
        identificationNumber: String,
        accountToken: Long,
        pageSize: Int,
        monthDate: String?
    ): Flow<PagingData<SmartMovement>> =
        repository.getPagedMovements(
            user,
            idBrand,
            identificationNumber,
            accountToken,
            pageSize,
            monthDate
        )
}
