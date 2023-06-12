package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.PromissoryNoteDetail
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QueryGetPromissoryNoteDetail {
    suspend fun invoke(
        idBrand: Int,
        idLoanClient: Int,
        pageNumber: Int,
        pageSize: Int,
        option: String
    ): Flow<MultimoneyResult<PromissoryNoteDetail?>>
}
