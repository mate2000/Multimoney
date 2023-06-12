package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.SaveSinpeAccount
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface MutationSaveSinpeAccountUseCase {
    suspend operator fun invoke(
        user: String,
        idBrand: Int,
        identification: String,
        accountNumber: String,
        idCurrency: Long,
        nameAccount: String,
        country: String,
        idAccount: Long?,
        option: String?,
        email: String?,
        isFavorite: Boolean?,
        idBank: Long? = null,
        typeAccount: Int? = null
    ): Flow<MultimoneyResult<SaveSinpeAccount?>>
}
