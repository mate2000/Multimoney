package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.ACHAccount
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SmartAccountRepository
import kotlinx.coroutines.flow.Flow

class MutationAddACHAccountUseCaseImpl(
    val repository: SmartAccountRepository
) : MutationAddACHAccountUseCase {
    override suspend fun invoke(
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
    ): Flow<MultimoneyResult<ACHAccount?>> = repository.mutationAddACHAccount(
        idBrand = idBrand,
        user = user,
        accountNumber = accountNumber,
        titularName = titularName,
        isFavorite = isFavorite,
        typeAccountId = typeAccountId,
        destinationBankId = destinationBankId,
        description = description,
        identificationNumber = identificationNumber,
        identificationTypeAccount = identificationTypeAccount,
        destinationCurrencyId = destinationCurrencyId,
        document = document
    )
}