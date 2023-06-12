package com.multimoney.domain.interaction.virtualcard

import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.virtualcard.ResendMicroDepositVD
import kotlinx.coroutines.flow.Flow

interface MutationResendMicroDepositVDUseCase {
    suspend operator fun invoke(
        identification: String,
        idCard: String,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<ResendMicroDepositVD?>>
}
