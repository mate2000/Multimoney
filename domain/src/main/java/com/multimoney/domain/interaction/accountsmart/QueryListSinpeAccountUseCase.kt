package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.SinpeAccountResult
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QueryListSinpeAccountUseCase {
    suspend operator fun invoke(
        user: String,
        idBrand: Int,
        identification: String,
        country: String,
        idAccount: Long,
        accountNumber: String
    ): Flow<MultimoneyResult<SinpeAccountResult?>>
}