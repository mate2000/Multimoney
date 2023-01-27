package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.SmartFavoriteResult
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface MutationUpdateFavoriteSmartUseCase {
    suspend operator fun invoke(
        idBrand: Int,
        user: String,
        idFavorite: Long?,
        idAccountType: Int?,
        idCustomer: Long,
        accountNumber: String,
        accountName: String?,
        email: String,
        active: Boolean,
        phoneNumber: String?,
        idCurrencyAccount: Int,
    ): Flow<MultimoneyResult<SmartFavoriteResult?>>
}