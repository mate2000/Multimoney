package com.multimoney.domain.interaction.virtualcard

import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.virtualcard.MicroDepositVD
import kotlinx.coroutines.flow.Flow

interface MutationMicroDepositVDUseCase {
    suspend operator fun invoke(
        identification: String,
        idCard: String,
        code: String,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<MicroDepositVD?>>
}
