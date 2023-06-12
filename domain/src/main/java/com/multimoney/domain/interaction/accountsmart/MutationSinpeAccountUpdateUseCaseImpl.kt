package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.SaveSinpeAccount
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SmartAccountRepository
import kotlinx.coroutines.flow.Flow

class MutationSinpeAccountUpdateUseCaseImpl(val repository: SmartAccountRepository) :
    MutationSinpeAccountUpdateUseCase {
    override suspend fun invoke(
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
    ): Flow<MultimoneyResult<SaveSinpeAccount?>> = repository.mutationManageSinpeAccountUpdate(
        user = user,
        idBrand = idBrand,
        identification = identification,
        accountNumber = accountNumber,
        idCurrency = idCurrency,
        nameAccount = nameAccount,
        idAccount = idAccount,
        isFavorite = isFavorite,
        idBank = idBank,
        typeAccount = typeAccount
    )
}