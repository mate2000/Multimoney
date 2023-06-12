package com.multimoney.domain.interaction.balance

import com.multimoney.domain.model.balance.BalanceCardInformation
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QueryBalanceCardInformationUseCase {
    suspend operator fun invoke(
        user: String,
        identification: String,
        idBrand: Int,
        idClient: Int,
        idLoanClient: Int,
        cardStatus: Int
    ): Flow<MultimoneyResult<BalanceCardInformation?>>
}
