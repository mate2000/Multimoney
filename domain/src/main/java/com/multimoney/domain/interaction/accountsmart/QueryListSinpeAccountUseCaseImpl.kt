package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.SinpeAccountResult
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SmartAccountRepository
import kotlinx.coroutines.flow.Flow


class QueryListSinpeAccountUseCaseImpl(val repository: SmartAccountRepository) :
    QueryListSinpeAccountUseCase {
    override suspend fun invoke(
        user: String,
        idBrand: Int,
        identification: String,
        country: String,
        idAccount: Long,
        accountNumber: String,
        isFavorite: Boolean?,
        option:String?
    ): Flow<MultimoneyResult<SinpeAccountResult?>> =
        repository.querySinpeAccount(
            user,
            idBrand,
            identification,
            country,
            idAccount,
            accountNumber,
            isFavorite,
            option
        )
}