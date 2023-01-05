package com.multimoney.domain.interaction.crypto

import com.multimoney.domain.model.crypto.CryptoCurrencyNews
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface GetCurrencyNewsUseCase {

    suspend operator fun invoke(
        baseAsset: String,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<CryptoCurrencyNews>>
}