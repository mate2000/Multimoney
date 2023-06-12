package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.SaveSinpeAccount
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SmartAccountRepository
import kotlinx.coroutines.flow.Flow

class MutationSaveSinpeAccountUseCaseImpl(val repository: SmartAccountRepository) :
    MutationSaveSinpeAccountUseCase {
    override suspend fun invoke(
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
        idBank: Long?,
        typeAccount: Int?
    ): Flow<MultimoneyResult<SaveSinpeAccount?>> = repository.mutationManageSinpeAccountSave(
        user = user,
        idBrand = idBrand,
        identification = identification,
        accountNumber = accountNumber,
        idCurrency = idCurrency,
        nameAccount = nameAccount,
        country = country,
        idAccount = idAccount,
        option = option,
        email = email,
        isFavorite = isFavorite,
        idBank = idBank,
        typeAccount = typeAccount
    )
}