package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.FavoriteACHResult
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QueryACHTransferFavoriteListUseCase {
    suspend operator fun invoke(
        user: String,
        idBrand: Int,
        isFavorite: Boolean,
        identification: String,
    ): Flow<MultimoneyResult<FavoriteACHResult?>>
}