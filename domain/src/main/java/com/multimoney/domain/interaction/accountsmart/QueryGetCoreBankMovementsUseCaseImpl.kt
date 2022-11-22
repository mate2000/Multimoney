package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.SmartMovementsResult
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SmartAccountRepository
import kotlinx.coroutines.flow.Flow

class QueryGetCoreBankMovementsUseCaseImpl(
    private val smartAccountRepository: SmartAccountRepository
) : QueryGetCoreBankMovementsUseCase {
    override suspend operator fun invoke(
        user: String,
        idBrand: Int,
        identificationNumber: String,
        accountToken: Long,
        pageNumber: Int,
        pageSize: Int,
        monthDate: String
    ): Flow<MultimoneyResult<SmartMovementsResult?>> =
        smartAccountRepository.queryGetCoreBankMovements(
            user,
            idBrand,
            identificationNumber,
            accountToken,
            pageNumber,
            pageSize,
            monthDate
        )
}
