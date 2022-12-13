package com.multimoney.domain.interaction.credit

import androidx.paging.PagingData
import com.multimoney.domain.model.credit.CreditMovement
import com.multimoney.domain.repository.CreditRepository
import kotlinx.coroutines.flow.Flow

class QueryGetPagedCreditMovementsUseCaseImpl(val repository: CreditRepository) : QueryGetPagedCreditMovementsUseCase {

    override suspend fun invoke(
        idBrand: Int,
        idLoanClient: Int,
        pageSize: Int,
        option: String
    ): Flow<PagingData<CreditMovement>> =
        repository.getPagedCreditMovements(
            idBrand,
            idLoanClient,
            pageSize,
            option
        )
}
