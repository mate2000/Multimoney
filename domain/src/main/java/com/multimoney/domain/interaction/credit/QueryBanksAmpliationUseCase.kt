package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.BanksAmpliationList
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QueryBanksAmpliationUseCase {
    suspend operator fun invoke(
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<BanksAmpliationList?>>
}