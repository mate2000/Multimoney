package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.SaveSinpeAccount
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface MutationSinpeAccountUpdateUseCase {
    suspend operator fun invoke(
        user: String,
        idBrand: Int,
        identification: String,
        accountNumber: String,
        idCurrency: Long,
        nameAccount: String,
        idAccount: Int?,
        isFavorite: Boolean,
        idBank: Long,
        typeAccount: Long
    ): Flow<MultimoneyResult<SaveSinpeAccount?>>
}