package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.LocalFavorite
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QueryLocalTransferFavoriteUseCase {
    suspend operator fun invoke(
        idBrand: Int,
        user: String,
        isFavorite: Boolean,
        idCustomer: Long
    ): Flow<MultimoneyResult<List<LocalFavorite?>?>>
}
