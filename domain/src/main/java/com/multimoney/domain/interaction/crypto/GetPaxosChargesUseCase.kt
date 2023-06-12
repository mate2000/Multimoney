package com.multimoney.domain.interaction.crypto

import com.multimoney.domain.model.crypto.GetPaxosChargeData
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface GetPaxosChargesUseCase {

    suspend operator fun invoke(
        user: String,
        idBrand: Int,
        key: String,
        transaction: String,
    ): Flow<MultimoneyResult<GetPaxosChargeData>>
}