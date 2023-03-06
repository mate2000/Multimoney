package com.multimoney.domain.interaction.security

import com.multimoney.domain.model.security.SaveLogTracking
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface MutationSaveLogTrackingUseCase {
    suspend operator fun invoke(
        identification: String,
        pkUser: Int,
        keySearch: String,
        data: String,
        idBrand: Int
    ): Flow<MultimoneyResult<SaveLogTracking>>
}
