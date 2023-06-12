package com.multimoney.domain.interaction.credit

import androidx.paging.PagingData
import com.multimoney.domain.model.credit.CreditMovement
import kotlinx.coroutines.flow.Flow

interface QueryGetPagedCreditMovementsUseCase {
    suspend operator fun invoke(
        idBrand: Int,
        idLoanClient: Int,
        pageSize: Int,
        option: String
    ): Flow<PagingData<CreditMovement>>
}
