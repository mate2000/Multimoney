package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.GetInfoDeposit
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.CreditRepository
import kotlinx.coroutines.flow.Flow

class QueryGetInfoDepositUseCaseImpl(val creditRepository: CreditRepository) : QueryGetInfoDepositUseCase {
    override suspend fun invoke(idBrand: Int, idPrint: Long, user: String): Flow<MultimoneyResult<GetInfoDeposit?>> =
        creditRepository.queryGetInfoDeposit(idBrand, idPrint, user)
}
