package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.GetInfoDeposit
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QueryGetInfoDepositUseCase {
    suspend operator fun invoke(
        idBrand: Int,
        idPrint: Long,
        user: String
    ): Flow<MultimoneyResult<GetInfoDeposit?>>
}
