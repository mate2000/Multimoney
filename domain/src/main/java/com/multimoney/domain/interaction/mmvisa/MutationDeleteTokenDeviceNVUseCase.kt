package com.multimoney.domain.interaction.mmvisa

import com.multimoney.domain.model.mmvisa.CardIssuanceNV
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface MutationDeleteTokenDeviceNVUseCase {
    suspend operator fun invoke(
        identification: String,
        user: String,
        idBrand: Int,
        idClient: Int,
        idLoanClient: Int,
        idDevice: String
    ): Flow<MultimoneyResult<CardIssuanceNV?>>
}
