package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.ACHAccount
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface MutationAddACHAccountUseCase {
    suspend operator fun invoke(
        idBrand: Int,
        user: String,
        accountNumber: String,
        titularName: String,
        isFavorite: Boolean,
        typeAccountId: Int,
        destinationBankId: Int,
        description: String,
        identificationNumber: String,
        identificationTypeAccount: Int,
        destinationCurrencyId: Int,
        document: String
    ): Flow<MultimoneyResult<ACHAccount?>>
}
