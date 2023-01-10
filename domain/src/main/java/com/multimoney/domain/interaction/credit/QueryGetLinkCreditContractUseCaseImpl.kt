package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.LinkCreditContract
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.CreditRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class QueryGetLinkCreditContractUseCaseImpl @Inject constructor(private val creditRepository: CreditRepository) :
    QueryGetLinkCreditContractUseCase {
    override suspend fun invoke(
        idPrint: Long,
        idBrand: Int,
        pkUser: Long,
        user: String
    ): Flow<MultimoneyResult<LinkCreditContract?>> =
        creditRepository.queryGetLinkCreditContract(
            idPrint = idPrint,
            idBrand = idBrand,
            pkUser = pkUser,
            user = user
        )
}