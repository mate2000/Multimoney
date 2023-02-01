package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.SmartFavoriteResult
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SmartAccountRepository
import kotlinx.coroutines.flow.Flow

class MutationUpdateFavoriteSmartUseCaseImpl(
    val repository: SmartAccountRepository
) : MutationUpdateFavoriteSmartUseCase {
    override suspend fun invoke(
        idBrand: Int,
        user: String,
        idFavorite: Long?,
        idAccountType: Int,
        idCustomer: Long,
        accountNumber: String,
        accountName: String?,
        email: String,
        active: Boolean,
        isFavorite: Boolean,
        phoneNumber: String?,
        idCurrencyAccount: Int?
    ): Flow<MultimoneyResult<SmartFavoriteResult?>> {
        return repository.mutationUpdateFavoriteContactSmart(
            idBrand = idBrand,
            user = user,
            idFavorite = idFavorite,
            idAccountType = idAccountType,
            idCustomer = idCustomer,
            accountNumber = accountNumber,
            accountName = accountName,
            email = email,
            active = active,
            isFavorite = isFavorite,
            phoneNumber = phoneNumber,
            idCurrencyAccount = idCurrencyAccount
        )
    }
}