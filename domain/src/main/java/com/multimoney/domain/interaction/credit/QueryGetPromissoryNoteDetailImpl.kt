package com.multimoney.domain.interaction.credit

import com.multimoney.domain.repository.CreditRepository

class QueryGetPromissoryNoteDetailImpl(private val creditRepository: CreditRepository) : QueryGetPromissoryNoteDetail {
    override suspend fun invoke(
        idBrand: Int,
        idLoanClient: Int,
        pageNumber: Int,
        pageSize: Int,
        option: String
    ) = creditRepository.queryGetPromissoryNoteDetail(
        idBrand,
        idLoanClient,
        pageNumber,
        pageSize,
        option
    )
}
