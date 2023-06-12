package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.LinkCreditContract
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QueryGetLinkCreditContractUseCase {
    suspend operator fun invoke(
        idPrint: Long,
        idBrand: Int,
        pkUser: Long,
        user: String
    ): Flow<MultimoneyResult<LinkCreditContract?>>
}