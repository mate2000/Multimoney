package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.HomeAddress
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QueryHomeAddressSVUseCase {
    suspend operator fun invoke(pkUser: String, user: String, idBrand: Int):
            Flow<MultimoneyResult<HomeAddress?>>
}