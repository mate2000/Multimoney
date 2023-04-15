package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface MutationUpdateACHAccountUseCase {
    suspend operator fun invoke(
        user: String,
        idBrand: Int,
        description: String,
        accountId: Int,
        titularName: String,
        identification: String,
        accountNumber: String
    ): Flow<MultimoneyResult<Int?>>
}