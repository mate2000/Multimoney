package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.BanksAndRegularExpression
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QueryBanksAndRegularExpressionUseCase {
    suspend operator fun invoke(
        pkUser: Int,
        user: String,
        idBrand: Int,
        idUserRequest: String
    ): Flow<MultimoneyResult<BanksAndRegularExpression>>
}