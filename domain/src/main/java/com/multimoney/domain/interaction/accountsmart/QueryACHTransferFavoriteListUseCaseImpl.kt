package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.FavoriteACHResult
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SmartAccountRepository
import kotlinx.coroutines.flow.Flow

class QueryACHTransferFavoriteListUseCaseImpl(
    private val repository: SmartAccountRepository
) : QueryACHTransferFavoriteListUseCase {
    override suspend fun invoke(
        user: String,
        idBrand: Int,
        isFavorite: Boolean,
        identificationNumber: String
    ): Flow<MultimoneyResult<FavoriteACHResult?>> = repository.queryACHTransferFavoriteList(
        user = user,
        idBrand = idBrand,
        isFavorite = isFavorite,
        identification = identificationNumber
    )
}